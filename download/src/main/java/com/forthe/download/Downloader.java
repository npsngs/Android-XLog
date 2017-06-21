package com.forthe.download;

import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Downloader {
    public static final int ERROR_NONE              = 8192;

    public static final int ERROR_UNKNOWN           = 1000;
    public static final int ERROR_URL               = 1001;
    public static final int ERROR_RANGE             = 1002;

    public static final int ERROR_CONNECT           = 2001;
    public static final int ERROR_SOCKET            = 2002;
    public static final int ERROR_PROTOCOL          = 2003;
    public static final int ERROR_METHOD            = 2004;
    public static final int ERROR_SERVER            = 2005;


    public static final int ERROR_OPENFILE          = 3001;
    public static final int ERROR_NOSPACE           = 3002;
    public static final int ERROR_TARGET_BROKEN     = 3003;

    public static final int ERROR_CONTENT           = 4001;


    public static DownloaderBuilder createBuilder(String uri, String targetFile){
        return new DownloaderBuilder(uri, targetFile);
    }

    private Downloader(){}
    private SpeedChangeListener speedChangeListener;
    private DownloadListener downloadListener;
    private DebugListener debugListener;
    private SrcBean srcBean;
    private UrlParser urlParser;
    private String targetFilePath;
    private File targetFile;
    private boolean isStop = false;
    private boolean isCheckMd5;
    private StatusBean statusBean;
    public void start(){
        isStop = false;
        int code = openTargetFile(targetFilePath);
        if(code != ERROR_NONE){
            handleCode(code);
        }else {
            download(srcBean);
        }
    }

    public void startAsync(){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                start();
            }
        });
    }

    public void stop(){
        isStop = true;
    }

    private void download(SrcBean srcBean){
        String uri = srcBean.getUri();
        int code;
        if(uri.startsWith("http://")){
            code = downloadFromHttp(srcBean);
        } else if(uri.startsWith("https://")){
            code = downloadFromHttps(srcBean);
        } else {
            code = ERROR_PROTOCOL;
        }

        handleCode(code);
    }

    private int openTargetFile(String path){
        /*设置目标文件*/
        try {
            if(targetFile == null){
                targetFile = new File(path);
                if(!targetFile.exists()){
                    File parents = new File(targetFile.getParent());
                    if(!parents.exists()){
                        parents.mkdirs();
                    }
                    targetFile.createNewFile();
                }
            }
            srcBean.setOffset(targetFile.length());
        } catch (IOException e) {
            e.printStackTrace();
            return ERROR_OPENFILE;
        }
        return ERROR_NONE;
    }



    private int downloadFromHttp(SrcBean srcBean){
        URL url = createUrl(srcBean);
        if(null == url){
            return ERROR_URL;
        }

        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return ERROR_CONNECT;
        }

        if(srcBean.getOffset() > 0L){
            // 设置范围，格式为Range：bytes x-y
            conn.setRequestProperty("Range", "bytes=" + srcBean.getOffset() + "-" );
        }
        conn.setRequestProperty("Accept-Encoding", "identity");
        conn.setReadTimeout(srcBean.getReadTimeout());//无限阻塞
        conn.setConnectTimeout(srcBean.getConnectTimeout());//十分钟
        conn.setFollowRedirects(true);
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
            conn.disconnect();
            return ERROR_METHOD;
        }

        try {
            conn.connect();
            if(debugListener != null){
                String debugLog = printConnection(conn);
                debugListener.onDebug(debugLog);
            }

            int respCode = conn.getResponseCode();
            if(200 == respCode || 206 == respCode){
                statusBean.setTotalSize(conn.getContentLength() + srcBean.getOffset());
                statusBean.setCompleteSize(srcBean.getOffset());
                statusBean.setMd5(conn.getHeaderField("Content-MD5"));
                if(downloadListener != null){
                    downloadListener.onStart(statusBean.getCompleteSize(), statusBean.getTotalSize());
                }

                FileOutputStream fos = new FileOutputStream(targetFile, statusBean.getCompleteSize()>0);


                return readFrom(conn.getInputStream(), fos);
            }else {
                conn.disconnect();
                return respCode;
            }
        }catch (IOException e) {
            e.printStackTrace();
            conn.disconnect();
            return ERROR_CONNECT;
        }
    }

    @Nullable
    private URL createUrl(SrcBean srcBean) {
        String uri = srcBean.getUri().trim().replace("\u00a0", "");//替换全角空格
        URL url = null;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private int readFrom(InputStream is, OutputStream os) {
        /*真正开始下载*/
        try {
            byte[] buffer = new byte[1024];
            int len;
            int byteCount = 0;
            long lastTime = SystemClock.elapsedRealtime();
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
                statusBean.addCompleteSize(len);



                if (null != speedChangeListener || null != downloadListener) {
                    byteCount += len;

                    long curTime = SystemClock.elapsedRealtime();
                    long passTime = curTime - lastTime;
                    if (passTime > 1000) {
                        if (null != speedChangeListener) {
                            float speed = byteCount * 125f / passTime /128;
                            speedChangeListener.onSpeedChange(speed);
                        }

                        if (null != downloadListener) {
                            downloadListener.onProgress(statusBean.getCompleteSize(), statusBean.getTotalSize());
                        }
                        byteCount = 0;
                        lastTime = curTime;
                    }
                }

                if (isStop) {
                    is.close();
                    if(null != downloadListener){
                        downloadListener.onStop(statusBean.getCompleteSize());
                    }
                    break;
                }
            }
            os.flush();

            /*下载完成*/
            if (-1 == len && statusBean.checkSize()) {
                String md5 = statusBean.getMd5();
                if(isCheckMd5 && !TextUtils.isEmpty(md5)){
                    String fileMd5 = getFileMD5(targetFile);
                    if(!md5.equals(fileMd5)){
                        return ERROR_TARGET_BROKEN;
                    }
                }

                if(null != downloadListener){
                    downloadListener.onFinish(statusBean.getTotalSize());
                }
                return ERROR_NONE;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(e instanceof SocketTimeoutException) {
                return ERROR_SOCKET;
            }else if(e instanceof SocketException){
                String msg = e.getMessage();
                if(null != msg && msg.contains("ECONNRESET")){
                    srcBean.setRetryCount(0);/*socket被断开需要重连*/
                }
                return ERROR_SOCKET;
            }else if(e instanceof UnknownHostException){
                return ERROR_SERVER;
            } else {
                String msg = e.getMessage();
                if(null != msg && msg.contains("ENOSPC")){
                    return ERROR_NOSPACE;
                }
                return ERROR_UNKNOWN;
            }
        }finally {
            try {
                is.close();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return ERROR_NONE;
    }

    private int downloadFromHttps(SrcBean srcBean){
        try {
            SSLContext ssl = SSLContext.getInstance("TLS");
            ssl.init(null, new TrustManager[]{new X509TrustManager(){
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(ssl.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return downloadFromHttp(srcBean);
    }

    private void handleCode(int code){
        if(code >= 1000){
            handleError(code);
        }else {
            handleRespCode(code);
        }
    }

    private void handleError(int errorCode){
        switch (errorCode){
            case ERROR_UNKNOWN:
            case ERROR_URL:
            case ERROR_PROTOCOL:
            case ERROR_METHOD:
            case ERROR_NOSPACE:
            case ERROR_OPENFILE:
            case ERROR_SERVER:
                if(downloadListener != null){
                    downloadListener.onError(errorCode);
                }
                break;
            case ERROR_RANGE:
                srcBean.setOffset(0);
                download(srcBean);
                break;
            case ERROR_CONNECT:
            case ERROR_SOCKET:
                if(srcBean.canRetry()){
                    try {
                        srcBean.retryCount++;
                        srcBean.setOffset(targetFile.length());
                        download(srcBean);
                    }catch (Exception e){
                        e.printStackTrace();
                        if(downloadListener != null){
                            downloadListener.onError(ERROR_UNKNOWN);
                        }
                    }
                }
                break;
            case ERROR_CONTENT:
            case ERROR_TARGET_BROKEN:
                if(downloadListener != null){
                    downloadListener.onError(errorCode);
                }
                break;
        }
    }

    private void handleRespCode(int respCode){
        switch (respCode){
            case 416:
                srcBean.setOffset(0);
                download(srcBean);
                break;
        }
    }

    private String printConnection(HttpURLConnection conn) {
        StringBuilder builder = new StringBuilder("  ----Download URLConnection Debug----\n");
        builder.append("[URL]:").append(conn.getURL().toString()).append("\n");
        builder.append("[RESPONSE]\n");
        builder.append("----------------------\n");
        Map<String, List<String>> headers = conn.getHeaderFields();
        if(null != headers){
            Set<String> keys = headers.keySet();
            for (String key:keys){
                List<String> values = headers.get(key);
                if(null == key){
                    builder.append(values.toString()).append("\n");
                }else{
                    builder.append(key).append(":").append(values.toString()).append("\n");
                }
            }
        }
        return builder.toString();
    }

    public interface DownloadListener{
        void onStart(long offset, long size);
        void onProgress(long offset, long size);
        void onStop(long offset);
        void onFinish(long size);
        void onError(int errorCode);
    }

    public interface SpeedChangeListener {
        void onSpeedChange(float bytesPerSec);
    }

    public interface DebugListener {
        void onDebug(String debugMsg);
    }

    public interface UrlParser{
        String parseFrom(String url);
    }

    public String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }

        MessageDigest digest;
        FileInputStream in;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        byte[] data = digest.digest();
        String md5 = new String(Base64.encode(data, Base64.DEFAULT));
        return md5.replaceAll("\n", "");
    }




    private static class StatusBean {
        private long totalSize = 0;
        private long completeSize = 0;
        private String md5;
        public long getTotalSize() {
            return totalSize;
        }

        public void setTotalSize(long totalSize) {
            this.totalSize = totalSize;
            checkSize();
        }

        public long getCompleteSize() {
            return completeSize;
        }

        public void setCompleteSize(long completeSize) {
            this.completeSize = completeSize;
            checkSize();
        }

        public void addCompleteSize(long len) {
            this.completeSize += len;
            checkSize();
        }

        private boolean checkSize(){
            if (completeSize > totalSize) {
                throw new IllegalStateException(String.format("target file broken completeSize:%d totalByte:%d", completeSize,totalSize));
            }
            return completeSize == totalSize;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }
    }


    private static class SrcBean {
        private String sourceUri;
        private String uri;
        private long offset;
        private int retryCount;
        private int maxRetry;
        private int readTimeout;
        private int connectTimeout;

        public SrcBean(String uri) {
            this.sourceUri = uri;
            this.uri = uri;
            maxRetry = 3;
            readTimeout = 10000;
            connectTimeout = 5000;
        }

        public String getSourceUri() {
            return sourceUri;
        }

        public void setSourceUri(String sourceUri) {
            this.sourceUri = sourceUri;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public long getOffset() {
            return offset;
        }

        public void setOffset(long offset) {
            this.offset = offset;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public boolean canRetry(){
            return retryCount <= maxRetry;
        }

        public void setRetryCount(int retryCount) {
            this.retryCount = retryCount;
        }

        public int getMaxRetry() {
            return maxRetry;
        }

        public void setMaxRetry(int maxRetry) {
            this.maxRetry = maxRetry;
        }

        public int getReadTimeout() {
            return readTimeout;
        }

        public void setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
        }

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }
    }

    public static class DownloaderBuilder{
        private DownloaderBuilder(String uri, String targetFile){
            this.srcBean = new SrcBean(uri);
            this.targetFilePath = targetFile;
        }
        private SpeedChangeListener speedChangeListener;
        private DownloadListener downloadListener;
        private DebugListener debugListener;
        private SrcBean srcBean;
        private UrlParser urlParser;
        private String targetFilePath;
        private boolean isCheckMd5 = false;
        public Downloader build(){
            Downloader downloader = new Downloader();
            downloader.srcBean = srcBean;
            downloader.targetFilePath = targetFilePath;
            downloader.downloadListener = downloadListener;
            downloader.speedChangeListener = speedChangeListener;
            downloader.debugListener = debugListener;
            downloader.urlParser = urlParser;
            downloader.statusBean = new StatusBean();
            downloader.isCheckMd5 = isCheckMd5;
            return downloader;
        }

        public DownloaderBuilder setCheckMd5(boolean checkMd5) {
            isCheckMd5 = checkMd5;
            return this;
        }

        public DownloaderBuilder setMaxRetry(int maxRetry) {
            srcBean.setMaxRetry(maxRetry);
            return this;
        }

        public DownloaderBuilder setReadTimeout(int readTimeout) {
            srcBean.setReadTimeout(readTimeout);
            return this;
        }

        public DownloaderBuilder setConnectTimeout(int connectTimeout) {
            srcBean.setConnectTimeout(connectTimeout);
            return this;
        }

        public DownloaderBuilder setSpeedChangeListener(SpeedChangeListener speedChangeListener) {
            this.speedChangeListener = speedChangeListener;
            return this;
        }

        public DownloaderBuilder setDownloadListener(DownloadListener downloadListener) {
            this.downloadListener = downloadListener;
            return this;
        }

        public DownloaderBuilder setDebugListener(DebugListener debugListener) {
            this.debugListener = debugListener;
            return this;
        }

        public DownloaderBuilder setUrlParser(UrlParser urlParser) {
            this.urlParser = urlParser;
            return this;
        }
    }

}
