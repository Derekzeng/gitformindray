package com.mindray.cis.third;

import com.mindray.egateway.model.PatientResult;

import java.io.IOException;

public interface IResult {
    public void pushRealtimeResult(PatientResult result) throws IOException;
}
