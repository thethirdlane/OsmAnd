package net.osmand.plus.views.controls.maphudbuttons;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.osmand.plus.configmap.ConfigureMapDialogs;
import net.osmand.plus.simulation.OsmAndLocationSimulation;
import net.osmand.plus.views.mapwidgets.configure.buttons.MapButtonState;
import net.osmand.plus.views.mapwidgets.configure.buttons.SimSpeedIncButtonState;
import net.osmand.plus.views.mapwidgets.configure.buttons.ZoomInButtonState;

public class SimSpeedIncButton extends MapButton {

	private final SimSpeedIncButtonState buttonState;
	OsmAndLocationSimulation locationSimulation = app.getLocationProvider().getLocationSimulation();

	public SimSpeedIncButton(@NonNull Context context) {
		this(context, null);
	}

	public SimSpeedIncButton(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SimSpeedIncButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		buttonState = app.getMapButtonsHelper().getSimSpeedIncButtonState();

		setOnClickListener(v -> {

			//AP - change speed when running
			var isSimRunning = locationSimulation != null;
			Log.i("ttl.simspeed", "Speed Increase Button Called, simrunning: " + isSimRunning);
			if(isSimRunning) {
				var oldSpeed = locationSimulation.getSpeed();
				var newIshSpeed = (oldSpeed * 3.6) + 1;
				newIshSpeed = Math.min(newIshSpeed, 260);

				var newSpeed = (float)(newIshSpeed / 3.6);

				//This will take care of setting the new speed on the slider
				settings.simulateNavigationSpeed = newSpeed;

				locationSimulation.setSpeed(newSpeed);
				Log.i("ttl.simspeed", "Speed Inc from " + oldSpeed + ", to: " + newSpeed + ", " + newIshSpeed);
			}
		});
	}

	@Nullable
	@Override
	public MapButtonState getButtonState() {
		return buttonState;
	}

	@Override
	protected boolean shouldShow() {
		return locationSimulation != null && locationSimulation.isRouteAnimating();
//		return true;
//		return !routeDialogOpened && visibilityHelper.shouldShowZoomButtons();
	}
}