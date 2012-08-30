package it.bisco.citatio;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public enum BoxType {
		Circle, Rounded, Square
	}

	Bitmap mImageBitmap;
	ImageView mView;
	int scaleFactory = 1;
	Draw mDraw;

	int tapX = 0;
	int tapY = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mView = (ImageView) findViewById(R.id.imageView1);

		// this.addContentView(mDraw, new
		// LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		final SeekBar seekBarRadius = (SeekBar) findViewById(R.id.seekBarRadius);
		final TextView textViewRadius = (TextView) findViewById(R.id.textViewRadius);

		// Instantiate an ImageView and define its properties
		Context context = getApplicationContext();
		mDraw = new Draw(this);
		try {
			mImageBitmap = BitmapFactory.decodeStream(this.getAssets().open("fabrizio_solo_pic.jpg"));
			mView.setImageBitmap(mImageBitmap);
			seekBarRadius.setMax(mImageBitmap.getWidth() >> 1); // /2

			tapX = mImageBitmap.getWidth() >> 1;
			tapY = mImageBitmap.getHeight() >> 1;

			seekBarRadius.setProgress(0);

		} catch (Exception e) {
			Toast.makeText(context, "Errore:" + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		// GRIGIO

		final Button btnSetGrey = (Button) findViewById(R.id.btnSetGrey);
		btnSetGrey.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bitmap grayscaleBitmap = Bitmap.createBitmap(mImageBitmap.getWidth(), mImageBitmap.getHeight(), Bitmap.Config.RGB_565);

				Canvas c = new Canvas(grayscaleBitmap);
				Paint p = new Paint();
				ColorMatrix cm = new ColorMatrix();

				cm.setSaturation(0.4f);
				ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cm);
				p.setColorFilter(filter);
				c.drawBitmap(mImageBitmap, 0, 0, p);
				mView.setImageBitmap(grayscaleBitmap);
			}
		});

		// OFFUSCA

		final Button btnSetObfuscation = (Button) findViewById(R.id.btnSetObfuscation);
		btnSetObfuscation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Bitmap bitmap = Bitmap.createBitmap(mImageBitmap.getWidth(), mImageBitmap.getHeight(), Bitmap.Config.ARGB_4444);

				Canvas canvas = new Canvas(bitmap);

				// Bitmap bmp = Bitmap.createScaledBitmap(bitmap, 100, 151,
				// true);
				int numberOfSteps = 10;
				int step = mImageBitmap.getWidth() / numberOfSteps;
				Rect Rect2 = new Rect(0, 0, step, mImageBitmap.getHeight());
				// Rect imageRect = new Rect(0, 0, mImageBitmap.getWidth(),
				// mImageBitmap.getHeight());

				Paint paint = new Paint();
				paint.setAlpha(255);

				canvas.drawBitmap(bitmap, null, Rect2, null);
				while (paint.getAlpha() != 0) {
					paint.setAlpha(paint.getAlpha() - (255 / numberOfSteps));
					canvas.drawBitmap(mImageBitmap, Rect2, Rect2, paint);
					Rect2.set(Rect2.left + step, Rect2.top, Rect2.right + step, Rect2.bottom);
				}
				mView.setImageBitmap(bitmap);
			}
		});

		// RADIAL GRADIENT

		final Button btnSetRadial = (Button) findViewById(R.id.btnSetRadial);
		btnSetRadial.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				int radius = seekBarRadius.getProgress();

				Bitmap bitmap = drawCitationImage(mImageBitmap, tapX, tapY, radius);

				mView.setImageBitmap(bitmap);
			}

			private Bitmap drawCitationImage(Bitmap srcImage, float circleX, float circleY, float radius) {

				// dimensioni
				int width = srcImage.getWidth();
				int height = srcImage.getHeight();

				// crop cerchio foto
				Bitmap croppedImage = Bitmap.createScaledBitmap(srcImage, width / scaleFactory, height / scaleFactory, true);
				// Bitmap croppedImage = Bitmap.createBitmap(width, height,
				// Bitmap.Config.ARGB_8888);
				Canvas croppedCanvas = new Canvas(croppedImage);

				// scala di grigi
				ColorMatrix cm = new ColorMatrix();
				cm.setSaturation(0.0f);
				ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cm);
				Paint paint = new Paint();
				paint.setColorFilter(filter);
				croppedCanvas.drawBitmap(mImageBitmap, 0, 0, paint);

				Path path = new Path();
				path.addCircle(circleX, circleY, radius, Path.Direction.CW);
				croppedCanvas.clipPath(path, Region.Op.DIFFERENCE);
				croppedCanvas.drawColor(0x00FFFFFF, PorterDuff.Mode.CLEAR);

				// immagine finale
				Bitmap outBitmap = Bitmap.createScaledBitmap(srcImage, width / scaleFactory, height / scaleFactory, true);
				Canvas outCanvas = new Canvas(outBitmap);

				// Copia del cerchio croppato sull'immagine nera
				outCanvas.drawBitmap(croppedImage, 0, 0, null);
				croppedImage.recycle();

				Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

				// cerchio sfumato
				RadialGradient gradient = new android.graphics.RadialGradient(circleX / scaleFactory, circleY / scaleFactory, radius / scaleFactory,
						0x00000000, 0xFF000000, android.graphics.Shader.TileMode.CLAMP);
				p = new Paint(Paint.ANTI_ALIAS_FLAG);
				p.setShader(gradient);
				p.setColor(0xFF000000);
				p.setXfermode(new PorterDuffXfermode(Mode.DARKEN));
				outCanvas.drawCircle(circleX, circleY, radius, p);

				// Add text
				p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
				p.setColor(Color.WHITE);
				p.setStyle(Paint.Style.FILL);
				p.setTextAlign(Paint.Align.RIGHT);

				p.setTextSize(50 / scaleFactory);
				// outCanvas.drawText("Finchè dura, fa verdura", width /
				// scaleFactory / 2, height / scaleFactory / 2, p);
				outCanvas.drawText("Finchè dura,", width / scaleFactory, height / scaleFactory / 2.0f, p);
				outCanvas.drawText("fa verdura", width / scaleFactory, height / scaleFactory / 2.0f + 40, p);

				p.setTextSize(30 / scaleFactory);
				outCanvas.drawText("Fabrizio Biscossi", width / scaleFactory * 0.90f, height - 100f, p);

				return outBitmap;
			}
		});

		// ORIGINALE

		final Button btnSetOriginal = (Button) findViewById(R.id.btnSetOriginal);
		btnSetOriginal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mView.setImageBitmap(mImageBitmap);
			}
		});

		mView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// calculate inverse matrix
				Matrix inverse = new Matrix();
				mView.getImageMatrix().invert(inverse);

				// map touch point from ImageView to image
				float[] touchPoint = new float[] { event.getX(), event.getY() };
				inverse.mapPoints(touchPoint);
				// touchPoint now contains x and y in image's coordinate system

				tapX = (int) touchPoint[0];
				tapY = (int) touchPoint[1];

				Bitmap bitmap = Bitmap.createScaledBitmap(mImageBitmap, mImageBitmap.getWidth() / scaleFactory, mImageBitmap.getHeight()
						/ scaleFactory, true);

				Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
				paint.setColor(Color.GREEN);
				paint.setStyle(Style.STROKE);
				paint.setStrokeWidth(2);

				Canvas canvas = new Canvas(bitmap);
				canvas.drawCircle(tapX, tapY, seekBarRadius.getProgress(), paint);
				mView.setImageBitmap(bitmap);

				return false;
			}
		});

		seekBarRadius.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				textViewRadius.setText(String.valueOf(progress));

				Bitmap bitmap = Bitmap.createScaledBitmap(mImageBitmap, mImageBitmap.getWidth() / scaleFactory, mImageBitmap.getHeight()
						/ scaleFactory, true);

				Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
				paint.setColor(Color.GREEN);
				paint.setStyle(Style.STROKE);
				paint.setStrokeWidth(2);

				Canvas canvas = new Canvas(bitmap);
				canvas.drawCircle(tapX, tapY, seekBarRadius.getProgress(), paint);
				mView.setImageBitmap(bitmap);

				// mDraw.setCords(tapX, tapY, seekBarRadius.getProgress());
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				mDraw.setCords(tapX, tapY, seekBarRadius.getProgress());
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mDraw.setCords(tapX, tapY, seekBarRadius.getProgress());
			}
		});

	}

	protected void DrawPathBox(Canvas canvas, int x, int y, int radius, BoxType type) {

		switch (type) {
		case Circle:
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(Color.GREEN);
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(4);

			canvas.drawCircle(tapX, tapY, radius, paint);
			//mView.setImageBitmap(bitmap);
			
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
