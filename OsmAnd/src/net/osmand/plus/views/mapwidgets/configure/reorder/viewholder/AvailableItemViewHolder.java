package net.osmand.plus.views.mapwidgets.configure.reorder.viewholder;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.osmand.plus.R;
import net.osmand.plus.utils.ColorUtilities;
import net.osmand.plus.views.controls.ReorderItemTouchHelperCallback.UnmovableItem;
import net.osmand.plus.views.mapwidgets.MapWidgetInfo;
import net.osmand.plus.views.mapwidgets.WidgetGroup;
import net.osmand.plus.views.mapwidgets.configure.reorder.viewholder.AddedWidgetViewHolder.AddedWidgetUiInfo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class AvailableItemViewHolder extends ViewHolder implements UnmovableItem {

	public final ImageButton addButton;
	public final ImageView icon;
	public final TextView title;
	public final View infoButton;
	public final View bottomDivider;

	public AvailableItemViewHolder(@NonNull View itemView) {
		super(itemView);
		addButton = itemView.findViewById(R.id.add_button);
		icon = itemView.findViewById(R.id.icon);
		title = itemView.findViewById(R.id.title);
		infoButton = itemView.findViewById(R.id.info_button);
		bottomDivider = itemView.findViewById(R.id.bottom_divider);
	}

	@Override
	public boolean isMovingDisabled() {
		return true;
	}

	@NonNull
	public static Spannable getGroupTitle(@NonNull WidgetGroup group, @NonNull Context context, boolean nightMode) {
		String groupName = context.getString(group.titleId);
		int widgetsCount = group.getWidgets().size();
		String title = context.getString(R.string.ltr_or_rtl_combine_via_dash, groupName, String.valueOf(widgetsCount));
		SpannableString spannableTitle = new SpannableString(title);
		int spanFlag = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;
		int primaryTextColor = ColorUtilities.getPrimaryTextColor(context, nightMode);
		int secondaryTextColor = ColorUtilities.getSecondaryTextColor(context, nightMode);

		int nameStartIndex = title.indexOf(groupName);
		if (nameStartIndex != -1) {
			spannableTitle.setSpan(new ForegroundColorSpan(secondaryTextColor), 0, title.length(), spanFlag);
			int nameEndIndex = nameStartIndex + groupName.length();
			spannableTitle.setSpan(new ForegroundColorSpan(primaryTextColor), nameStartIndex, nameEndIndex, spanFlag);
		}

		return spannableTitle;
	}

	public static class AvailableWidgetUiInfo {

		public String key;
		public String title;
		public MapWidgetInfo info;
		public int order;
		public int iconId;

		public AvailableWidgetUiInfo() {
		}

		public AvailableWidgetUiInfo(@NonNull AddedWidgetUiInfo addedWidgetUiInfo, int order) {
			this.key = addedWidgetUiInfo.key;
			this.title = addedWidgetUiInfo.title;
			this.info = addedWidgetUiInfo.info;
			this.order = order;
			this.iconId = addedWidgetUiInfo.iconId;
		}
	}
}