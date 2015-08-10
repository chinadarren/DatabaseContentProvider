package com.example.DatabaseContentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by cl on 2015/8/10.
 */
public class DatabaseProvider extends ContentProvider {
    public static final int BOOK_DIR = 0;
    public static final int BOOK_ITEM = 1;
    public static final int CATEGORY_DIR = 2;
    public static final int CATEGORY_ITEM = 3;
    public static final String AUTHORITY = "com.example.databasetest.provider";
    private static UriMatcher uriMatcher;
    private MyDatabaseHelper dbHelper;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,"book",BOOK_DIR);
        uriMatcher.addURI(AUTHORITY,"book/#",BOOK_ITEM);
        uriMatcher.addURI(AUTHORITY,"category",CATEGORY_DIR);
        uriMatcher.addURI(AUTHORITY,"category/#",CATEGORY_ITEM);
    }
    @Override
    public boolean onCreate(){
        dbHelper = new MyDatabaseHelper(getContext(),"BookStore.db",null,3);
        return true;
    }
    @Override
    public Cursor query(Uri uri,String[] projection,String selection,String[] selectionArgs,String sortOreder){
        //查询数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)){
            case BOOK_DIR:
                cursor = db.query("book",projection,selection,selectionArgs,null,null,sortOreder);
            break;
            case BOOK_ITEM:
                //getPathSegments将内容URI权限后的部分以“/”符号分割
                //分割后放入到字符一个字符串列表中，第0个位置是路径，第1个位置是id
                String bookId = uri.getPathSegments().get(1);
                cursor = db.query("Book",projection,"id = ?",new String[] {bookId},null,null,sortOreder);
                break;
            case CATEGORY_DIR:
                cursor = db.query("Category",projection,selection,selectionArgs,null,null,sortOreder);
                break;
            case CATEGORY_ITEM:
                String categryId = uri.getPathSegments().get(1);
                cursor = db.query("Category",projection,"id = ?",new String[] {categryId},null,null,sortOreder);
                break;
            default:
                break;
        }
        return cursor;
    }
    @Override
    public Uri insert(Uri uri,ContentValues values){
        //添加数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri uriReturn = null;
        switch (uriMatcher.match(uri)){
            case  BOOK_DIR:
            case BOOK_ITEM:
                long newBookId = db.insert("Book", null, values);
                uriReturn = Uri.parse("content://" + AUTHORITY + "/book/" + newBookId);
                break;
            case CATEGORY_DIR:
            case CATEGORY_ITEM:
                long newCategory = db.insert("Category",null,values);
                //intsert方法要求返回一个能够表示这条新增数据的URI
                //Uir.parse方法将一个内容RUI解析成Uri对象
                //URI是以新增数据的id结尾
                uriReturn = Uri.parse("content://"+AUTHORITY+"/CATEGORY/"+newCategory);
                break;
            default:
                break;
        }
        return uriReturn;
    }
    @Override
    public int update(Uri uri,ContentValues values,String selection,String[] selectionArgs){
        //更新数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updatedRows = 0;
        switch (uriMatcher.match(uri)){
            case BOOK_DIR:
                updatedRows = db.update("BOOK",values,selection,selectionArgs);
                break;
            case BOOK_ITEM:
                String bookId = uri.getPathSegments().get(1);
                updatedRows = db.update("Bokk",values,"id = ?",new String[] {bookId});
                break;
            case CATEGORY_DIR:
                updatedRows = db.update("Category",values,selection,selectionArgs);
                break;
            case CATEGORY_ITEM:
                String categoryId = uri.getPathSegments().get(1);
                updatedRows = db.update("Category", values, "id = ?", new String[]{categoryId});
                break;
            default:
                break;
        }
        return updatedRows;
    }
    @Override
    public int delete(Uri uri,String selection,String[] selectionArgs){
        //删除数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = 0 ;
        switch (uriMatcher.match(uri)){
            case BOOK_DIR:
                deletedRows = db.delete("Book", selection, selectionArgs);
                break;
            case BOOK_ITEM:
                String bookId = uri.getPathSegments().get(1);
                deletedRows = db.delete("Book", "id = ?", new String[]{bookId});
                break;
            case CATEGORY_DIR:
                deletedRows = db.delete("Category", selection, selectionArgs);
                break;
            case CATEGORY_ITEM:
                String categoryId = uri.getPathSegments().get(1);
                deletedRows = db.delete("Category", "id = ?", new String[]{categoryId});
            break;
            default:
                break;
        }
        return deletedRows;
    }
    @Override
    public String getType(Uri uri){
        switch (uriMatcher.match(uri)){
            case BOOK_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.DatabaseContentProvider.provider.book";
            case BOOK_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.DatabaseContentProvider.provider.book";
            case CATEGORY_DIR:
                return "vnd.android.corsor.dir/vnd.com.example.DatabaseContentProvider.provider.category";
            case CATEGORY_ITEM:
                return "vnd.android.corsor.item/vnd.com.example.DatabaseContentProvider.provider.category";
        }
        return null;
    }
}
