package cn.itcast.wh.mdmusic2.common;

import android.media.session.MediaController;

/**
 * Created by Administrator on 2015/9/3.
 *
 * 2个抽象方法
 */
public interface IMediaControllerConsumer {

    /**
     * 当绑定服务成功，有了mediaController对象的时候
     * @param mediaController
     */
    public void OnObtainMediaController(MediaController mediaController);

    /**
     * 当停止服务，mediaController对象不需要了的时候
     * @param mediaController
     */
    public void OnReleasedMediaController(MediaController mediaController);

}
