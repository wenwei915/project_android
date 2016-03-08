package com.mrwo.notebook.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/12/5.
 */
public class AudioInfo {
    public ArrayList<WsBean> ws;

    public class WsBean{
        public ArrayList<CwBean> cw;
    }

    public class CwBean{
        public String w;
    }

}
