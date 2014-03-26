package com.zelwise.spiewnik;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import android.util.Base64;
import android.util.Log;

public class SerializeHelper {
	public static String serializeObjectToString(Object object) {
		if (object != null) {
			byte[] serializedObject = serializeObject(object);
			if (serializedObject != null) {
				return Base64.encodeToString(serializedObject, Base64.DEFAULT);
			}
		}
		return "";
	}

	public static Object deserializeObjectFromString(String objectString) {
		byte[] bytes = Base64.decode(objectString, Base64.DEFAULT);
		return deserializeObject(bytes);
	}

	public static byte[] serializeObject(Object object) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(object);
			out.close();

			// Get the bytes of the serialized object
			byte[] buf = bos.toByteArray();

			return buf;
		} catch (IOException ioe) {
			Log.e("serializeObject", "error", ioe); 
			return null;
		}
	}

	public static Object deserializeObject(byte[] bytesArray) {
		try {
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytesArray));
			Object object = in.readObject();
			in.close();

			return object;
		} catch (ClassNotFoundException cnfe) {
			return null;
		} catch (IOException ioe) {
			return null;
		}
	}

}
