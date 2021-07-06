package org.unibl.etf.service;

public class TokenServiceProxy implements org.unibl.etf.service.TokenService {
  private String _endpoint = null;
  private org.unibl.etf.service.TokenService tokenService = null;
  
  public TokenServiceProxy() {
    _initTokenServiceProxy();
  }
  
  public TokenServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initTokenServiceProxy();
  }
  
  private void _initTokenServiceProxy() {
    try {
      tokenService = (new org.unibl.etf.service.TokenServiceServiceLocator()).getTokenService();
      if (tokenService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)tokenService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)tokenService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (tokenService != null)
      ((javax.xml.rpc.Stub)tokenService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.unibl.etf.service.TokenService getTokenService() {
    if (tokenService == null)
      _initTokenServiceProxy();
    return tokenService;
  }
  
  public java.lang.String getToken(org.unibl.etf.model.Person person) throws java.rmi.RemoteException{
    if (tokenService == null)
      _initTokenServiceProxy();
    return tokenService.getToken(person);
  }
  
  public java.lang.String getPassword(org.unibl.etf.model.Person person) throws java.rmi.RemoteException{
    if (tokenService == null)
      _initTokenServiceProxy();
    return tokenService.getPassword(person);
  }
  
  public java.lang.String[] getTokens() throws java.rmi.RemoteException{
    if (tokenService == null)
      _initTokenServiceProxy();
    return tokenService.getTokens();
  }
  
  public boolean updatePerson(org.unibl.etf.model.Person person) throws java.rmi.RemoteException{
    if (tokenService == null)
      _initTokenServiceProxy();
    return tokenService.updatePerson(person);
  }
  
  public boolean logOut(org.unibl.etf.model.Person person) throws java.rmi.RemoteException{
    if (tokenService == null)
      _initTokenServiceProxy();
    return tokenService.logOut(person);
  }
  
  public boolean checkTokenValidity(java.lang.String uuid) throws java.rmi.RemoteException{
    if (tokenService == null)
      _initTokenServiceProxy();
    return tokenService.checkTokenValidity(uuid);
  }
  
  
}