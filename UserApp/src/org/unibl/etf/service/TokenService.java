/**
 * TokenService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.unibl.etf.service;

public interface TokenService extends java.rmi.Remote {
    public java.lang.String getToken(org.unibl.etf.model.Person person) throws java.rmi.RemoteException;
    public java.lang.String getPassword(org.unibl.etf.model.Person person) throws java.rmi.RemoteException;
    public java.lang.String[] getTokens() throws java.rmi.RemoteException;
    public boolean updatePerson(org.unibl.etf.model.Person person) throws java.rmi.RemoteException;
    public boolean logOut(org.unibl.etf.model.Person person) throws java.rmi.RemoteException;
    public boolean checkTokenValidity(java.lang.String uuid) throws java.rmi.RemoteException;
}
