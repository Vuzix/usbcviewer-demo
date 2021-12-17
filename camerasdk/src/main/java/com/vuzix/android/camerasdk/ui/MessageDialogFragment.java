package com.vuzix.android.camerasdk.ui;
/*
 * libcommon
 * utility/helper classes for myself
 *
 * Copyright (c) 2014-2018 saki t_saki@serenegiant.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
*/

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.vuzix.android.camerasdk.utils.BuildCheck;

public class MessageDialogFragment extends DialogFragment {
	private static final String TAG = MessageDialogFragment.class.getSimpleName();

	public interface MessageDialogListener {
		void onMessageDialogResult(final MessageDialogFragment dialog, final int requestCode, final String[] permissions, final boolean result);
	}

	public static void showDialog(final FragmentActivity parent, final int requestCode, final int id_title, final int id_message, final String[] permissions) {
		final MessageDialogFragment dialog = newInstance(requestCode, id_title, id_message, permissions);
		dialog.show(parent.getSupportFragmentManager(), TAG);
	}

	public static MessageDialogFragment showDialog(final Fragment parent, final int requestCode, final int id_title, final int id_message, final String[] permissions) {
		final MessageDialogFragment dialog = newInstance(requestCode, id_title, id_message, permissions);
		dialog.setTargetFragment(parent, parent.getId());
		dialog.show(parent.getParentFragmentManager(), TAG);
		return dialog;
	}

	public static MessageDialogFragment newInstance(final int requestCode, final int id_title, final int id_message, final String[] permissions) {
		final MessageDialogFragment fragment = new MessageDialogFragment();
		final Bundle args = new Bundle();
		args.putInt("requestCode", requestCode);
		args.putInt("title", id_title);
		args.putInt("message", id_message);
		args.putStringArray("permissions", permissions != null ? permissions : new String[]{});
		fragment.setArguments(args);
		return fragment;
	}

	private MessageDialogListener mDialogListener;

	public MessageDialogFragment() {
		super();
	}


	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		if (requireActivity() instanceof MessageDialogListener) {
			mDialogListener = (MessageDialogListener) requireActivity();
		}
		if (mDialogListener == null) {
			final Fragment fragment = getTargetFragment();
			if (fragment instanceof MessageDialogListener) {
				mDialogListener = (MessageDialogListener) fragment;
			}
		}
		if (mDialogListener == null) {
			if (BuildCheck.isAndroid4_2()) {
				final Fragment target = getParentFragment();
				if (target instanceof MessageDialogListener) {
					mDialogListener = (MessageDialogListener)target;
				}
			}
		}
		if (mDialogListener == null) {
			throw new ClassCastException(requireActivity().toString());
		}
	}

	@NonNull
	@Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
		final Bundle args = savedInstanceState != null ? savedInstanceState : requireArguments();
		final int requestCode = requireArguments().getInt("requestCode");
		final int id_title = requireArguments().getInt("title");
		final int id_message = requireArguments().getInt("message");
		final String[] permissions = args.getStringArray("permissions");


		return new AlertDialog.Builder(getActivity())
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(id_title)
			.setMessage(id_message)
			.setPositiveButton(android.R.string.ok,
					(dialog, whichButton) -> {
						try {
							mDialogListener.onMessageDialogResult(MessageDialogFragment.this, requestCode, permissions, true);
						} catch (final Exception e) {
							Log.w(TAG, e);
						}
					}
			)
			.setNegativeButton(android.R.string.cancel,
					(dialog, whichButton) -> {
						try {
							mDialogListener.onMessageDialogResult(MessageDialogFragment.this, requestCode, permissions, false);
						} catch (final Exception e) {
							Log.w(TAG, e);
						}
					}
			)
			.create();
	}

}
