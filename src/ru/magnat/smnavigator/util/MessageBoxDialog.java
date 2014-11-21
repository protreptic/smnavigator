package ru.magnat.smnavigator.util;

import android.app.Dialog;
import android.content.Context;

public class MessageBoxDialog extends Dialog {

	public static final int MB_ABORTRETRYIGNORE = 0x00000002;
	public static final int MB_CANCELTRYCONTINUE = 0x00000006;
	public static final int MB_HELP = 0x00004000;
	public static final int MB_OK = 0x00000000;
	public static final int MB_OKCANCEL = 0x00000001;
	public static final int MB_RETRYCANCEL = 0x00000005;
	public static final int MB_YESNO = 0x00000004;
	public static final int MB_YESNOCANCEL = 0x00000003;
	
	public static final int IDABORT = 3;
	public static final int IDCANCEL = 2;
	public static final int IDCONTINUE = 11;
	public static final int IDIGNORE = 5;
	public static final int IDNO = 7;
	public static final int IDOK = 1;
	public static final int IDRETRY = 4;
	public static final int IDTRYAGAIN = 10;
	public static final int IDYES = 6;
	
	public static final int MB_DEFBUTTON1 = 0x00000000;
	public static final int MB_DEFBUTTON2 = 0x00000100;
	public static final int MB_DEFBUTTON3 = 0x00000200;
	public static final int MB_DEFBUTTON4 = 0x00000300;
	
	public static final int MB_ICONHAND = 0x00000010;
	public static final int MB_ICONSTOP = 0x00000010;
	public static final int MB_ICONERROR = 0x00000010;
	public static final int MB_ICONQUESTION = 0x00000020;
	public static final int MB_ICONEXCLAMATION = 0x00000030;
	public static final int MB_ICONWARNING = 0x00000030;
	public static final int MB_ICONASTERISK  = 0x00000040;
	public static final int MB_ICONINFORMATION  = 0x00000040;
	
	public MessageBoxDialog(Context context) {
		super(context);
		
		initialize();
	}

	public MessageBoxDialog(Context context, int theme) {
		super(context, theme);
		
		initialize();
	}
	
	public MessageBoxDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		
		initialize();
	}
	
	private void initialize() {
		
	}
	
}
