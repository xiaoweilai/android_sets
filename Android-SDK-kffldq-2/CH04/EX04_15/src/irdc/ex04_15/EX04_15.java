package irdc.ex04_15; /* import���class */ import java.util.Calendar; import android.app.Activity; import android.os.Bundle; import android.widget.TextView; import android.widget.DatePicker; import android.widget.TimePicker; public class EX04_15 extends Activity { /*�������ڼ�ʱ�����*/ private int mYear; private int mMonth; private int mDay; private int mHour; private int mMinute; /*����������*/ TextView tv; TimePicker tp; DatePicker dp; /** Called when the activity is first created. */ @Override public void onCreate(Bundle savedInstanceState) { /*ȡ��Ŀǰ������ʱ��*/ Calendar c=Calendar.getInstance(); mYear=c.get(Calendar.YEAR); mMonth=c.get(Calendar.MONTH); mDay=c.get(Calendar.DAY_OF_MONTH); mHour=c.get(Calendar.HOUR_OF_DAY); mMinute=c.get(Calendar.MINUTE); super.onCreate(savedInstanceState); /* ����main.xml Layout */ setContentView(R.layout.main); /*ȡ��TextView���󣬲�����updateDisplay()���趨��ʾ�ĳ�ʼ����ʱ��*/ tv= (TextView) findViewById(R.id.showTime); updateDisplay(); /*ȡ��DatePicker������init()�趨��ʼֵ��onDateChangeListener() */ dp=(DatePicker)findViewById(R.id.dPicker); dp.init(mYear, mMonth, mDay, new DatePicker.OnDateChangedListener() { @Override public void onDateChanged(DatePicker view,int year,int monthOfYear, int dayOfMonth) { mYear=year; mMonth= monthOfYear; mDay=dayOfMonth; /*����updateDisplay()���ı���ʾ����*/ updateDisplay(); } }); /*ȡ��TimePicker���󣬲��趨Ϊ24Сʱ����ʾ*/ tp=(TimePicker)findViewById(R.id.tPicker); tp.setIs24HourView(true); /*setOnTimeChangedListener������дonTimeChanged event*/ tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() { @Override public void onTimeChanged(TimePicker view,int hourOfDay,int minute) { mHour=hourOfDay; mMinute=minute; /*����updateDisplay()���ı���ʾʱ��*/ updateDisplay(); } }); } /*�趨��ʾ����ʱ��ķ���*/ private void updateDisplay() { tv.setText ( new StringBuilder().append(mYear).append("/") .append(format(mMonth + 1)).append("/") .append(format(mDay)).append("��") .append(format(mHour)).append(":") .append(format(mMinute)) ); } /*����ʱ����ʾ��λ���ķ���*/ private String format(int x) { String s=""+x; if(s.length()==1) s="0"+s; return s; } }