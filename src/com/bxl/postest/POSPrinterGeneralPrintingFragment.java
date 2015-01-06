package com.bxl.postest;

import java.util.ArrayList;

import jpos.JposException;
import jpos.POSPrinterConst;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class POSPrinterGeneralPrintingFragment extends POSPrinterFragment
		implements View.OnClickListener, ListDialogFragment.OnClickListener {
	
	private EditText sendToPrinterEditText;
	
	private static String ESCAPE_SEQUENCE = new String(new byte[] {0x1b, 0x7c});
	private static String[] ESCAPE_SEQUENCES = new String[] {
			"ABCDEF\n",
			ESCAPE_SEQUENCE + "uCABCDEF\n",
			ESCAPE_SEQUENCE + "1CABCDEF\n",
			ESCAPE_SEQUENCE + "2CABCDEF\n",
			ESCAPE_SEQUENCE + "3CABCDEF\n",
			ESCAPE_SEQUENCE + "4CABCDEF\n",
			ESCAPE_SEQUENCE + "1hC" + ESCAPE_SEQUENCE + "1vCABCDEF\n",
			ESCAPE_SEQUENCE + "8hC" + ESCAPE_SEQUENCE + "1vCABCDEF\n",
			ESCAPE_SEQUENCE + "8hCABCDEF\n",
			ESCAPE_SEQUENCE + "N\n",
			ESCAPE_SEQUENCE + "rAABCDEF\n",
			ESCAPE_SEQUENCE + "lAABCDEF\n",
			ESCAPE_SEQUENCE + "8hCABCD\n"
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pos_printer_general_printing, container, false);
		
		sendToPrinterEditText = (EditText) view.findViewById(R.id.editTextSendToPrinter);
		
		view.findViewById(R.id.buttonAddEscapeSequence).setOnClickListener(this);
		view.findViewById(R.id.buttonPrintNormal).setOnClickListener(this);
		view.findViewById(R.id.buttonCutPaper).setOnClickListener(this);
		view.findViewById(R.id.buttonCharacterSet).setOnClickListener(this);
		
		deviceMessagesTextView = (TextView) view.findViewById(R.id.textViewDeviceMessages);
		deviceMessagesTextView.setMovementMethod(new ScrollingMovementMethod());
		deviceMessagesTextView.setVerticalScrollBarEnabled(true);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonAddEscapeSequence:
			ListDialogFragment.showDialog(getFragmentManager(), "Escape sequences", ESCAPE_SEQUENCES);
			break;
			
		case R.id.buttonPrintNormal:
			try {
				posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, sendToPrinterEditText.getText().toString());
			} catch (JposException e) {
				e.printStackTrace();
				MessageDialogFragment.showDialog(getFragmentManager(), "Excepction", e.getMessage());
			}
			break;
			
		case R.id.buttonCutPaper:
			try {
				posPrinter.cutPaper(90);
			} catch (JposException e) {
				e.printStackTrace();
				MessageDialogFragment.showDialog(getFragmentManager(), "Excepction", e.getMessage());
			}
			break;
			
		case R.id.buttonCharacterSet:
			try {
				String characterSetList = posPrinter.getCharacterSetList();
				ArrayList<String> arrayList = new ArrayList<String>();
				for (String token : characterSetList.split(",")) {
					arrayList.add(token);
				}
				String[] items = arrayList.toArray(new String[arrayList.size()]);
				ListDialogFragment.showDialog(getFragmentManager(), getString(R.string.character_set), items);
			} catch (JposException e) {
				e.printStackTrace();
				MessageDialogFragment.showDialog(getFragmentManager(), "Excepction", e.getMessage());
			}
			
			break;
		}
	}
	
	@Override
	public void onClick(String title, String text) {
		if (title.equals("Escape sequences")) {
			sendToPrinterEditText.append(text);
		} else {
			try {
				posPrinter.setCharacterSet(Integer.valueOf(text));
			} catch (NumberFormatException | JposException e) {
				e.printStackTrace();
				MessageDialogFragment.showDialog(getFragmentManager(), "Excepction", e.getMessage());
			}
		}
	}
}
