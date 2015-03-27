package es.cctvdroid.mjpg;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MjpegInputStream extends DataInputStream {
	
	private final byte[] SOI_MARKER = { (byte) 0xFF, (byte) 0xD8 };
	private final byte[] EOF_MARKER = { (byte) 0xFF, (byte) 0xD9 };
	private final String CONTENT_LENGTH = "Content-Length";
	private final static int HEADER_MAX_LENGTH = 100;
	private final static int FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH;
	private int mContentLength = -1;
	
//	private static MjpegInputStream mis;
//	private static boolean salir = false;
	
	public MjpegInputStream(InputStream in) {
		super(new BufferedInputStream(in, FRAME_MAX_LENGTH));
	}
	
//	public static MjpegInputStream read(final String url) {
//		Runnable r = new Runnable() {
//			@Override
//			public void run() {
//				salir = false;
//				
//				// TODO Le asignamos un timeout a la conexión
//				HttpParams httpParameters = new BasicHttpParams();
//				HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
//				HttpConnectionParams.setSoTimeout(httpParameters, 5000);
//				
//				try {
//					DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
//					URI uri = URI.create(url);
//					HttpGet httpGet = new HttpGet(uri);
//					HttpResponse res = httpclient.execute(httpGet);
//					mis = new MjpegInputStream(res.getEntity().getContent());
//				}
//				catch (ClientProtocolException e) {
//					mis = null;
//				}
//				catch(IOException ex) {
//					mis = null;
//				}
//				catch(Exception ex) {
//					Log.e("Error!", ex.getMessage(), ex);
//					mis = null;
//				}
//				
//				salir = true;
//			}
//		};
//		
//		Thread t = new Thread(r);
//		t.start();
//		
//		if(!salir)
//			sleep(100);
//
//		return mis;
//	}

//	public static void getMjpegStream(final String url) {
//		Runnable r = new Runnable() {
//			@Override
//			public void run() {
//				
//				// TODO Le asignamos un timeout a la conexión
//				HttpParams httpParameters = new BasicHttpParams();
//				HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
//				HttpConnectionParams.setSoTimeout(httpParameters, 5000);
//				
//				try {
//					httpclient = new DefaultHttpClient(httpParameters);
//					uri = URI.create(url);
//					httpGet = new HttpGet(uri);
//					res = httpclient.execute(httpGet);
//					mis = new MjpegInputStream(res.getEntity().getContent());
//				}
//				catch (ClientProtocolException e) {
//					mis = null;
//				}
//				catch(IOException ex) {
//					mis = null;
//				}
//				catch(Exception ex) {
//					Log.e("Error!", ex.getMessage(), ex);
//					mis = null;
//				}
//			}
//		};
//		
//		t = new Thread(r);
//		t.start();
//	}
	
	

	private int getEndOfSeqeunce(DataInputStream in, byte[] sequence) throws IOException {
		int seqIndex = 0;
		byte c;
		for (int i = 0; i < FRAME_MAX_LENGTH; i++) {
			c = (byte) in.readUnsignedByte();
			if (c == sequence[seqIndex]) {
				seqIndex++;
				if (seqIndex == sequence.length)
					return i + 1;
			}
			else
				seqIndex = 0;
		}
		return -1;
	}

	private int getStartOfSequence(DataInputStream in, byte[] sequence) throws IOException {
		int end = getEndOfSeqeunce(in, sequence);
		return (end < 0) ? (-1) : (end - sequence.length);
	}

	private int parseContentLength(byte[] headerBytes) throws IOException, NumberFormatException {
		ByteArrayInputStream headerIn = new ByteArrayInputStream(headerBytes);
		Properties props = new Properties();
		props.load(headerIn);
		return Integer.parseInt(props.getProperty(CONTENT_LENGTH));
	}

	public Bitmap readMjpegFrame() throws IOException {
		mark(FRAME_MAX_LENGTH);
		int headerLen = getStartOfSequence(this, SOI_MARKER);
		reset();
		byte[] header = new byte[headerLen];
		readFully(header);
		try {
			mContentLength = parseContentLength(header);
		}
		catch (NumberFormatException nfe) {
			mContentLength = getEndOfSeqeunce(this, EOF_MARKER);
		}
		reset();
		byte[] frameData = new byte[mContentLength];
		skipBytes(headerLen);
		readFully(frameData);
		return BitmapFactory.decodeStream(new ByteArrayInputStream(frameData));
	}
	
//	private static void sleep(long millis) {
//		try {
//			Thread.sleep(millis);
//		}
//		catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
}
