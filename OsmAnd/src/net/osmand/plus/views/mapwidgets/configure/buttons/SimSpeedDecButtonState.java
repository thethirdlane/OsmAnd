package net.osmand.plus.views.mapwidgets.configure.buttons;

import static net.osmand.aidlapi.OsmAndCustomizationConstants.SIM_SPEED_DEC_HUD_ID;
import static net.osmand.plus.views.controls.maphudbuttons.ButtonPositionSize.POS_BOTTOM;
import static net.osmand.plus.views.controls.maphudbuttons.ButtonPositionSize.POS_RIGHT;

import androidx.annotation.NonNull;

import net.osmand.plus.OsmandApplication;
import net.osmand.plus.R;
import net.osmand.plus.settings.backend.preferences.CommonPreference;
import net.osmand.plus.views.controls.maphudbuttons.ButtonPositionSize;

//AP - for simulated Navigation Speed
public class SimSpeedDecButtonState extends MapButtonState {

	private final CommonPreference<Boolean> visibilityPref;

	public SimSpeedDecButtonState(@NonNull OsmandApplication app) {
		super(app, SIM_SPEED_DEC_HUD_ID);
		this.visibilityPref = addPreference(settings.registerBooleanPreference(id + "_state", true)).makeProfile();
	}

	@NonNull
	@Override
	public String getName() {
		return app.getString(R.string.simSpeedDec);
	}

	@NonNull
	@Override
	public String getDescription() {
		return app.getString(R.string.sim_speed_dec_action_descr);
	}

	@Override
	public boolean isEnabled() {
		return visibilityPref.get();
	}

	@NonNull
	@Override
	public CommonPreference<Boolean> getVisibilityPref() {
		return visibilityPref;
	}

	@Override
	public int getDefaultLayoutId() {
		return R.layout.sim_speed_dec_button;
	}

	@NonNull
	@Override
	public String getDefaultIconName() {
		return "ic_action_arrow_down";
	}

	@NonNull
	@Override
	protected ButtonPositionSize setupButtonPosition(@NonNull ButtonPositionSize position) {
		return setupButtonPosition(position, POS_RIGHT, POS_BOTTOM, false, true);
	}
}