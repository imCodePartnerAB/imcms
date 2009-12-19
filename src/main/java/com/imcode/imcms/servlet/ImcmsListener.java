package com.imcode.imcms.servlet;

public interface ImcmsListener {

    void onImcmsStart();

    void onImcmsStop();

    void onImcmsStartEx(Exception ex);

    void onImcmsModeChange(ImcmsMode newMode);    
}
