package cn.itcast.wh.mdmusic2.common;

import android.media.session.MediaController;

/**
 * Created by Administrator on 2015/9/3.
 *
 * 2�����󷽷�
 */
public interface IMediaControllerConsumer {

    /**
     * ���󶨷���ɹ�������mediaController�����ʱ��
     * @param mediaController
     */
    public void OnObtainMediaController(MediaController mediaController);

    /**
     * ��ֹͣ����mediaController������Ҫ�˵�ʱ��
     * @param mediaController
     */
    public void OnReleasedMediaController(MediaController mediaController);

}
