/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\yuyan\\GitCode\\ZNTSpeaker\\WiFiModel\\src\\main\\aidl\\com\\znt\\wifimodel\\IWifiCallback.aidl
 */
package com.znt.wifimodel;
// Declare any non-default types here with import statements

public interface IWifiCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.znt.wifimodel.IWifiCallback
{
private static final java.lang.String DESCRIPTOR = "com.znt.wifimodel.IWifiCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.znt.wifimodel.IWifiCallback interface,
 * generating a proxy if needed.
 */
public static com.znt.wifimodel.IWifiCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.znt.wifimodel.IWifiCallback))) {
return ((com.znt.wifimodel.IWifiCallback)iin);
}
return new com.znt.wifimodel.IWifiCallback.Stub.Proxy(obj);
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
case TRANSACTION_actionPerformed:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
this.actionPerformed(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.znt.wifimodel.IWifiCallback
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
/**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
@Override public void actionPerformed(int actionId, java.lang.String arg1, java.lang.String arg2) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(actionId);
_data.writeString(arg1);
_data.writeString(arg2);
mRemote.transact(Stub.TRANSACTION_actionPerformed, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_actionPerformed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
/**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
public void actionPerformed(int actionId, java.lang.String arg1, java.lang.String arg2) throws android.os.RemoteException;
}
