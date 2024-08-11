package com.mindray.cis.third;

import com.mindray.egateway.model.MdmResult;

import java.io.IOException;

public interface IMDM {
    public void pushMdmMsg(MdmResult result) throws IOException;
}
