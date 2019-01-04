/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\yuyan\\GitCode\\ZNTSpeaker\\ZNTDownLoadModel\\src\\com\\znt\\download\\IDownloadAidlInterface.aidl
 */
package com.znt.download;
public interface IDownloadAidlInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.znt.download.IDownloadAidlInterface
{
private static final java.lang.String DESCRIPTOR = "com.znt.download.IDownloadAidlInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.znt.download.IDownloadAidlInterface interface,
 * generating a proxy if needed.
 */
public static com.znt.download.IDownloadAidlInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.znt.download.IDownloadAidlInterface))) {
return ((com.znt.download.IDownloadAidlInterface)iin);
}
return new com.znt.download.IDownloadAidlInterface.Stub.Proxy(obj);
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
case TRANSACTION_addSongInfor:
{
data.enforceInterface(DESCRIPTOR);
com.znt.diange.mina.entity.SongInfor _arg0;
if ((0!=data.readInt())) {
_arg0 = com.znt.diange.mina.entity.SongInfor.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.addSongInfor(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_addSongInfors:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<com.znt.diange.mina.entity.SongInfor> _arg0;
_arg0 = data.createTypedArrayList(com.znt.diange.mina.entity.SongInfor.CREATOR);
this.addSongInfors(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_updateSaveDir:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.updateSaveDir(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
com.znt.download.IDownloadCallback _arg0;
_arg0 = com.znt.download.IDownloadCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
com.znt.download.IDownloadCallback _arg0;
_arg0 = com.znt.download.IDownloadCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.znt.download.IDownloadAidlInterface
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
@Override public void addSongInfor(com.znt.diange.mina.entity.SongInfor infor) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((infor!=null)) {
_data.writeInt(1);
infor.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_addSongInfor, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void addSongInfors(java.util.List<com.znt.diange.mina.entity.SongInfor> infors) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeTypedList(infors);
mRemote.transact(Stub.TRANSACTION_addSongInfors, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateSaveDir(java.lang.String dir) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(dir);
mRemote.transact(Stub.TRANSACTION_updateSaveDir, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void registerCallback(com.znt.download.IDownloadCallback cb) throws android.os.RemoteException
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
@Override public void unregisterCallback(com.znt.download.IDownloadCallback cb) throws android.os.RemoteException
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
static final int TRANSACTION_addSongInfor = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_addSongInfors = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_updateSaveDir = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public void addSongInfor(com.znt.diange.mina.entity.SongInfor infor) throws android.os.RemoteException;
public void addSongInfors(java.util.List<com.znt.diange.mina.entity.SongInfor> infors) throws android.os.RemoteException;
public void updateSaveDir(java.lang.String dir) throws android.os.RemoteException;
public void registerCallback(com.znt.download.IDownloadCallback cb) throws android.os.RemoteException;
public void unregisterCallback(com.znt.download.IDownloadCallback cb) throws android.os.RemoteException;
}
