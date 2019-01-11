package org.caiqizhao.util;

/**
 * 文件传输监听接口
 */
public interface FileTransferListener {
    //进度条
    void onProgress(int progress);

    //传输完成
    void onSucceess();

    //传输失败
    void onFailed();


    //传输暂停
    void onPaused();

    //传输取消
    void onCanceled();
}
