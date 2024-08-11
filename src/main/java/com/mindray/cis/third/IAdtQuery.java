package com.mindray.cis.third;

import com.mindray.egateway.model.AdtQueryException;
import com.mindray.egateway.model.AdtRequest;
import com.mindray.egateway.model.AdtResponse;

import java.util.List;

public interface IAdtQuery {
    public List<AdtResponse> querySync(AdtRequest request) throws AdtQueryException;
}
