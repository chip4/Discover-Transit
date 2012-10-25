package com.discovertransit;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class ChangeCityDialog extends DialogFragment {

	/* The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks.
	 * Each method passes the DialogFragment in case the host needs to query it. */
	public interface ChangeCityDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog);
		public void onDialogNegativeClick(DialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	static ChangeCityDialogListener mListener;

	private ArrayList<String> listItems=new ArrayList<String>();
	private String choice;
	
	public ChangeCityDialog(ArrayList<String> listItems) {
		this.listItems = listItems;
	}

	/* Call this to instantiate a new ChangeCityDialog.
	 * @param activity  The activity hosting the dialog, which must implement the
	 *                  ChangeCityDialogListener to receive event callbacks.
	 * @returns A new instance of ChangeCityDialog.
	 * @throws  ClassCastException if the host activity does not
	 *          implement ChangeCityDialogListener
	 */
	public static ChangeCityDialog newInstance(Activity activity,ArrayList<String> listItems) {
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the ChangeCityDialogListener so we can send events with it
			mListener = (ChangeCityDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement ChangeCityDialogListener");
		}
		ChangeCityDialog frag = new ChangeCityDialog(listItems);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1,listItems);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.city_label);
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				ChangeCityDialog.this.setChoice(listItems.get(which));
				mListener.onDialogPositiveClick(ChangeCityDialog.this);
			}
		});
		return builder.create();
	}
	
	public String getChoice() {
		return this.choice;
	}
	
	public void setChoice(String choice) {
		this.choice = choice;
	}
}
