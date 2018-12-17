/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Android\\Github\\ZNTSpeaker\\ZNTPushModel\\src\\com\\znt\\push\\ITaskCallback.aidl
 */
package com.znt.push;
// Declare any non-default types here with import statements

public interface ITaskCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.znt.push.ITaskCallback
{
private static final java.lang.String DESCRIPTOR = "com.znt.push.ITaskCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.znt.push.ITaskCallback interface,
 * generating a proxy if needed.
 */
public static com.znt.push.ITaskCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.znt.push.ITaskCallback))) {
return ((com.znt.push.ITaskCallback)iin);
}
return new com.znt.push.ITaskCallback.Stub.Proxy(obj);
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
java.lang.String _arg3;
_arg3 = data.readString();
this.actionPerformed(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.znt.push.ITaskCallback
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
@Override public void actionPerformed(int actionId, java.lang.String arg1, java.lang.String arg2, java.lang.String arg3) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(actionId);
_data.writeString(arg1);
_data.writeString(arg2);
_data.writeString(arg3);
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
public void actionPerformed(int actionId, java.lang.String arg1, java.lang.String arg2, java.lang.String arg3) throws android.os.RemoteException;
}
