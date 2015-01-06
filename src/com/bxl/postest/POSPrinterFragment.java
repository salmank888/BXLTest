package com.bxl.postest;

import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.events.ErrorEvent;
import jpos.events.ErrorListener;
import jpos.events.OutputCompleteEvent;
import jpos.events.OutputCompleteListener;
import jpos.events.StatusUpdateEvent;
import jpos.events.StatusUpdateListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.widget.TextView;

public abstract class POSPrinterFragment extends Fragment
		implements StatusUpdateListener, OutputCompleteListener, ErrorListener {
	
	protected POSPrinter posPrinter;
	
	protected TextView deviceMessagesTextView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		posPrinter = ((MainActivity) getActivity()).getPOSPrinter();
	}

	@Override
	public void statusUpdateOccurred(final StatusUpdateEvent e) {
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				deviceMessagesTextView.append("SUE: " + getSUEMessage(e.getStatus()) + "\n");
				scroll();
			}
		});
	}

	@Override
	public void outputCompleteOccurred(final OutputCompleteEvent e) {
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				deviceMessagesTextView.append("OCE: " + e.getOutputID() + "\n");
				scroll();
			}
		});
	}

	@Override
	public void errorOccurred(final ErrorEvent e) {
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				deviceMessagesTextView.append("Error: " + e + "\n");
				scroll();
			}
		});
	}
	
	private void scroll() {
		Layout layout = deviceMessagesTextView.getLayout();
		if (layout != null) {
			int y = layout.getLineTop(
					deviceMessagesTextView.getLineCount()) - deviceMessagesTextView.getHeight();
			if (y > 0) {
				deviceMessagesTextView.scrollTo(0, y);
				deviceMessagesTextView.invalidate();
			}
		}
	}
	
	private String getSUEMessage(int status){
		switch(status){
		case POSPrinterConst.PTR_SUE_COVER_OPEN:
			return "Cover Open";
			
		case POSPrinterConst.PTR_SUE_COVER_OK:
			return "Cover OK";
			
		case POSPrinterConst.PTR_SUE_REC_EMPTY:
			return "Receipt Paper Empty";
			
		case POSPrinterConst.PTR_SUE_REC_NEAREMPTY:
			return "Receipt Paper Near Empty";
			
		case POSPrinterConst.PTR_SUE_REC_PAPEROK:
			return "Receipt Paper OK";
			
		case POSPrinterConst.PTR_SUE_IDLE:
			return "Printer Idle";
			
			default:
				return "Unknown";
		}
	}
}
