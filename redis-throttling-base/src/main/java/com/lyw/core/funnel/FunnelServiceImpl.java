package com.lyw.core.funnel;

import com.lyw.core.ThrottlingService;

public class FunnelServiceImpl implements ThrottlingService {


    @Override
    public boolean canAccess(String token) {
        return false;
    }
}
