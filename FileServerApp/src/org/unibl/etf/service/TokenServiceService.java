/**
 * TokenServiceService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.unibl.etf.service;

public interface TokenServiceService extends javax.xml.rpc.Service {
    public java.lang.String getTokenServiceAddress();

    public org.unibl.etf.service.TokenService getTokenService() throws javax.xml.rpc.ServiceException;

    public org.unibl.etf.service.TokenService getTokenService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
