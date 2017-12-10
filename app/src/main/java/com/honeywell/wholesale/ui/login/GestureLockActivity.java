package com.honeywell.wholesale.ui.login;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.lib.gesturelock.GestureLock;
import com.honeywell.wholesale.lib.gesturelock.GestureLockView;
import com.honeywell.wholesale.lib.gesturelock.NexusStyleLockView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class GestureLockActivity extends Activity {

	private GestureLock gestureView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_lock);
		
		gestureView = (GestureLock) findViewById(R.id.gesture_lock);
		gestureView.setAdapter(new GestureLock.GestureLockAdapter() {
			// 手势解锁的宽高数量
			@Override
			public int getDepth() {
				return 4;
			}

			@Override
			public int[] getCorrectGestures() {
				return new int[]{0, 3, 6, 7, 8, 5, 2, 1, 4};
			}

			// 最大可重试次数
			@Override
			public int getUnmatchedBoundary() {
				return 5;
			}

			//block之前的间隔大小
			@Override
			public int getBlockGapSize(){
				return 10;
			}

			@Override
			public GestureLockView getGestureLockViewInstance(Context context, int position) {
//				if(position % 2 == 0){
//					return new MyStyleLockView(context);
//				}else{
					return new NexusStyleLockView(context);
//				}
			}
		});

		gestureView.setOnGestureEventListener(new GestureLock.OnGestureEventListener(){

			@Override
			public void onGestureEvent(boolean matched) {
				Toast.makeText(GestureLockActivity.this, "Match:" + matched, Toast.LENGTH_SHORT).show();
				gestureView.clear();
			}

			@Override
			public void onUnmatchedExceedBoundary() {
				Toast.makeText(GestureLockActivity.this, "输入5次错误!30秒后才能输入", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onBlockSelected(int position) {
				Log.d("position", position + "");
			}
			
		});
	}


}
