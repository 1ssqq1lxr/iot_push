package com.lxr.iot.ssl;

import java.io.InputStream;

public class StreamReader {

	
	public String toByteArray(InputStream fin)
	{
		int i = -1;
		StringBuilder buf = new StringBuilder();
		try{
			while((i=fin.read())!=-1){
				if(buf.length()>0) buf.append(",");
				buf.append("(byte)");
				buf.append(i);
			}

		}catch(Throwable e){
			;
		}
		
		return buf.toString();
	}
	
	public static void main(String[] args) {
		StreamReader reader = new StreamReader();
		System.out.println(reader.toByteArray(StreamReader.class.getResourceAsStream("/securesocket.jks")));

	}

}
