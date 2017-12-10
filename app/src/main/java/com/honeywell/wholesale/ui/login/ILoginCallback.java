package com.honeywell.wholesale.ui.login;

import com.honeywell.hybridapp.framework.event.WholesaleHttpResponse;

/**
 * Created by e887272 on 6/22/16.
 */
public interface ILoginCallback {

    public void onSuccessCallback(String loginResponse);

    public void onErrorCallback(WholesaleHttpResponse responseString);


}
