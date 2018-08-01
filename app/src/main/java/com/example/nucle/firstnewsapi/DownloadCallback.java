package com.example.nucle.firstnewsapi;

import android.os.Message;

/**
 * Defines several constants used between {@link NetworkFragment} and the UI.
 */

public interface DownloadCallback {

    interface Codes {

        int STATE_NONE = 101;
        int STATE_IGNORE = 102;
        int STATE_CONNECTING = 103;
        int STATE_DOWNLOAD_COMPLETE = 104;
        int SECOND_ARGUMENT_STATE = 550;
        int STATE_CLEARING = 105;

        int HTTP_OK_CODE = 200;
        int HTTP_SERVER_UNKNOWN = 879;
        int FIRST_ARGUMENT_STATE = 890;
        int HTTP_ERROR_MALFORMED_REQUEST = 400;
        int HTTP_ERROR_UNAUTHORISED = 401;
        int HTTP_ERROR_NOT_FOUND = 404;
        int HTTP_SERVER_ERROR = 500;

    }

    /**
     * Indicates that the callback handler needs to update its appearance or information based on
     * the result of the task. Expected to be called from the main thread.
     */
    void updateControlMessageFromDownload(Message msg);


    /**
     * Indicates that the download operation has finished. This method is called even if the
     * download hasn't completed successfully.
     */
    void finishDownloading();
}