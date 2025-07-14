package net.osmand.plus.views.controls.maphudbuttons;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.osmand.plus.simulation.OsmAndLocationSimulation;
import net.osmand.plus.views.mapwidgets.configure.buttons.MapButtonState;
import net.osmand.plus.views.mapwidgets.configure.buttons.SimSpeedDecButtonState;

public class SimSpeedDecButton extends MapButton {

	private final SimSpeedDecButtonState buttonState;
	OsmAndLocationSimulation locationSimulation = app.getLocationProvider().getLocationSimulation();

	public SimSpeedDecButton(@NonNull Context context) {
		this(context, null);
	}

	public SimSpeedDecButton(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SimSpeedDecButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		buttonState = app.getMapButtonsHelper().getSimSpeedDecButtonState();

		setOnClickListener(v -> {

			//AP - change speed when running
			var isSimRunning = locationSimulation != null;
			Log.i("ttl.simspeed", "Speed Decrease Button Called, simRunning: " + isSimRunning);
			if(isSimRunning) {
//				int oldSpeed = (int)locationSimulation.getSpeed();
//				var newSpeed = Math.max(oldSpeed - 1, 0);
//
//				settings.simulateNavigationSpeed = newSpeed;

				var oldSpeed = locationSimulation.getSpeed();
				var newIshSpeed = (oldSpeed * 3.6) - 1;
				//TODO - get rid of the hardcoded '5' value.  We got that
				// From the default value for the slider in the simulation settings
				newIshSpeed = Math.max(newIshSpeed, 5);

				var newSpeed = (float)(newIshSpeed / 3.6);

				//This will take care of setting the new speed on the slider
				settings.simulateNavigationSpeed = newSpeed;

				locationSimulation.setSpeed(newSpeed);
				Log.i("ttl.simspeed", "Speed Dec from " + oldSpeed + ", to: " + newSpeed + ", " + newIshSpeed);
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