/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\yuyan\\eclipse\\studio\\ZNTSpeaker\\WificonnectorSample\\src\\com\\jflavio1\\wificonnectorsample\\IWifiAidlInterface.aidl
 */
package com.jflavio1.wificonnectorsample;
public interface IWifiAidlInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.jflavio1.wificonnectorsample.IWifiAidlInterface
{
private static final java.lang.String DESCRIPTOR = "com.jflavio1.wificonnectorsample.IWifiAidlInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.jflavio1.wificonnectorsample.IWifiAidlInterface interface,
 * generating a proxy if needed.
 */
public static com.jflavio1.wificonnectorsample.IWifiAidlInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.jflavio1.wificonnectorsample.IWifiAidlInterface))) {
return ((com.jflavio1.wificonnectorsample.IWifiAidlInterface)iin);
}
return new com.jflavio1.wificonnectorsample.IWifiAidlInterface.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_startConnectWifi:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.startConnectWifi(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setWifiInfor:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.setWifiInfor(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getCurWifiName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getCurWifiName();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getCurWifiPwd:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getCurWifiPwd();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_isHasWifi:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _result = this.isHasWifi(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_devStatusCheck:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.devStatusCheck(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
com.jflavio1.wificonnectorsample.ITaskCallback _arg0;
_arg0 = com.jflavio1.wificonnectorsample.ITaskCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
com.jflavio1.wificonnectorsample.ITaskCallback _arg0;
_arg0 = com.jflavio1.wificonnectorsample.ITaskCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.jflavio1.wificonnectorsample.IWifiAidlInterface
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void startConnectWifi(java.lang.String wifiName, java.lang.String wifiPwd) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(wifiName);
_data.writeString(wifiPwd);
mRemote.transact(Stub.TRANSACTION_startConnectWifi, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setWifiInfor(java.lang.String wifiName, java.lang.String wifiPwd) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(wifiName);
_data.writeString(wifiPwd);
mRemote.transact(Stub.TRANSACTION_setWifiInfor, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getCurWifiName() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurWifiName, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getCurWifiPwd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurWifiPwd, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean isHasWifi(java.lang.String wifiName, java.lang.String wifiPwd) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(wifiName);
_data.writeString(wifiPwd);
mRemote.transact(Stub.TRANSACTION_isHasWifi, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void devStatusCheck(boolean isOnline) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isOnline)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_devStatusCheck, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void registerCallback(com.jflavio1.wificonnectorsample.ITaskCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterCallback(com.jflavio1.wificonnectorsample.ITaskCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_startConnectWifi = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_setWifiInfor = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getCurWifiName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getCurWifiPwd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_isHasWifi = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_devStatusCheck = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
}
public void startConnectWifi(java.lang.String wifiName, java.lang.String wifiPwd) throws android.os.RemoteException;
public void setWifiInfor(java.lang.String wifiName, java.lang.String wifiPwd) throws android.os.RemoteException;
public java.lang.String getCurWifiName() throws android.os.RemoteException;
public java.lang.String getCurWifiPwd() throws android.os.RemoteException;
public boolean isHasWifi(java.lang.String wifiName, java.lang.String wifiPwd) throws android.os.RemoteException;
public void devStatusCheck(boolean isOnline) throws android.os.RemoteException;
public void registerCallback(com.jflavio1.wificonnectorsample.ITaskCallback cb) throws android.os.RemoteException;
public void unregisterCallback(com.jflavio1.wificonnectorsample.ITaskCallback cb) throws android.os.RemoteException;
}
