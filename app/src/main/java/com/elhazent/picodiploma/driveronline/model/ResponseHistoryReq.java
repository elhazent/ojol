package com.elhazent.picodiploma.driveronline.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseHistoryReq{

	@SerializedName("result")
	private String result;

	@SerializedName("msg")
	private String msg;

	@SerializedName("data")
	private List<DataItem> data;

	public void setResult(String result){
		this.result = result;
	}

	public String getResult(){
		return result;
	}

	public void setMsg(String msg){
		this.msg = msg;
	}

	public String getMsg(){
		return msg;
	}

	public void setData(List<DataItem> data){
		this.data = data;
	}

	public List<DataItem> getData(){
		return data;
	}

	@Override
 	public String toString(){
		return 
			"ResponseHistoryReq{" + 
			"result = '" + result + '\'' + 
			",msg = '" + msg + '\'' + 
			",data = '" + data + '\'' + 
			"}";
		}
}