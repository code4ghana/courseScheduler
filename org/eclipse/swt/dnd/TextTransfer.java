package org.eclipse.swt.dnd;

import org.eclipse.swt.internal.ole.win32.FORMATETC;
import org.eclipse.swt.internal.ole.win32.IDataObject;
import org.eclipse.swt.internal.ole.win32.STGMEDIUM;
import org.eclipse.swt.internal.win32.OS;

public class TextTransfer extends ByteArrayTransfer
{
  private static TextTransfer _instance = new TextTransfer();
  private static final String CF_UNICODETEXT = "CF_UNICODETEXT";
  private static final String CF_TEXT = "CF_TEXT";
  private static final int CF_UNICODETEXTID = 13;
  private static final int CF_TEXTID = 1;

  public static TextTransfer getInstance()
  {
    return _instance;
  }

  public void javaToNative(Object paramObject, TransferData paramTransferData)
  {
    if ((!checkText(paramObject)) || (!isSupportedType(paramTransferData)))
      DND.error(2003);
    paramTransferData.result = -2147467259;
    String str = (String)paramObject;
    int i;
    char[] arrayOfChar;
    int j;
    int k;
    switch (paramTransferData.type)
    {
    case 13:
      i = str.length();
      arrayOfChar = new char[i + 1];
      str.getChars(0, i, arrayOfChar, 0);
      j = arrayOfChar.length * 2;
      k = OS.GlobalAlloc(64, j);
      OS.MoveMemory(k, arrayOfChar, j);
      paramTransferData.stgmedium = new STGMEDIUM();
      paramTransferData.stgmedium.tymed = 1;
      paramTransferData.stgmedium.unionField = k;
      paramTransferData.stgmedium.pUnkForRelease = 0;
      paramTransferData.result = 0;
      break;
    case 1:
      i = str.length();
      arrayOfChar = new char[i + 1];
      str.getChars(0, i, arrayOfChar, 0);
      j = OS.GetACP();
      k = OS.WideCharToMultiByte(j, 0, arrayOfChar, -1, null, 0, null, null);
      if (k == 0)
      {
        paramTransferData.stgmedium = new STGMEDIUM();
        paramTransferData.result = -2147221402;
        return;
      }
      int m = OS.GlobalAlloc(64, k);
      OS.WideCharToMultiByte(j, 0, arrayOfChar, -1, m, k, null, null);
      paramTransferData.stgmedium = new STGMEDIUM();
      paramTransferData.stgmedium.tymed = 1;
      paramTransferData.stgmedium.unionField = m;
      paramTransferData.stgmedium.pUnkForRelease = 0;
      paramTransferData.result = 0;
    }
  }

  public Object nativeToJava(TransferData paramTransferData)
  {
    if ((!isSupportedType(paramTransferData)) || (paramTransferData.pIDataObject == 0))
      return null;
    IDataObject localIDataObject = new IDataObject(paramTransferData.pIDataObject);
    localIDataObject.AddRef();
    FORMATETC localFORMATETC = paramTransferData.formatetc;
    STGMEDIUM localSTGMEDIUM = new STGMEDIUM();
    localSTGMEDIUM.tymed = 1;
    paramTransferData.result = getData(localIDataObject, localFORMATETC, localSTGMEDIUM);
    localIDataObject.Release();
    if (paramTransferData.result != 0)
      return null;
    int i = localSTGMEDIUM.unionField;
    try
    {
      int j;
      switch (paramTransferData.type)
      {
      case 13:
        j = OS.GlobalSize(i) / 2 * 2;
        if (j != 0)
          break;
      case 1:
        while (true)
        {
          return null;
          char[] arrayOfChar1 = new char[j / 2];
          int m = OS.GlobalLock(i);
          if (m != 0)
          {
            try
            {
              OS.MoveMemory(arrayOfChar1, m, j);
              int n = arrayOfChar1.length;
              for (int i1 = 0; i1 < arrayOfChar1.length; i1++)
                if (arrayOfChar1[i1] == 0)
                {
                  n = i1;
                  break;
                }
              String str2 = new String(arrayOfChar1, 0, n);
              return str2;
            }
            finally
            {
              OS.GlobalUnlock(i);
            }
            j = OS.GlobalLock(i);
            if (j != 0)
              try
              {
                int k = OS.GetACP();
                m = OS.MultiByteToWideChar(k, 1, j, -1, null, 0);
                if (m != 0)
                {
                  char[] arrayOfChar2 = new char[m - 1];
                  OS.MultiByteToWideChar(k, 1, j, -1, arrayOfChar2, arrayOfChar2.length);
                  String str1 = new String(arrayOfChar2);
                  return str1;
                }
              }
              finally
              {
                OS.GlobalUnlock(i);
              }
          }
        }
      }
    }
    finally
    {
      OS.GlobalFree(i);
    }
    jsr -10;
    return null;
  }

  protected int[] getTypeIds()
  {
    return new int[] { 13, 1 };
  }

  protected String[] getTypeNames()
  {
    return new String[] { "CF_UNICODETEXT", "CF_TEXT" };
  }

  boolean checkText(Object paramObject)
  {
    return (paramObject != null) && ((paramObject instanceof String)) && (((String)paramObject).length() > 0);
  }

  protected boolean validate(Object paramObject)
  {
    return checkText(paramObject);
  }
}

/* Location:           /Users/jeff.kusi/Downloads/AutomatedScheduler - Fakhry & Kusi.jar
 * Qualified Name:     org.eclipse.swt.dnd.TextTransfer
 * JD-Core Version:    0.6.2
 */