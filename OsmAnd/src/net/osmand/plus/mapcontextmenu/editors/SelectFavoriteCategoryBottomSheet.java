package net.osmand.plus.mapcontextmenu.editors;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;

import net.osmand.GPXUtilities.PointsCategory;
import net.osmand.GPXUtilities.WptPt;
import net.osmand.plus.utils.AndroidUtils;
import net.osmand.GPXUtilities;
import net.osmand.plus.utils.ColorUtilities;
import net.osmand.plus.myplaces.FavouritesDbHelper;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.R;
import net.osmand.plus.utils.UiUtilities;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.base.MenuBottomSheetDialogFragment;
import net.osmand.plus.base.bottomsheetmenu.BaseBottomSheetItem;
import net.osmand.plus.base.bottomsheetmenu.BottomSheetItemWithDescription;
import net.osmand.plus.base.bottomsheetmenu.SimpleBottomSheetItem;
import net.osmand.plus.base.bottomsheetmenu.simpleitems.DividerItem;
import net.osmand.plus.helpers.AndroidUiHelper;
import net.osmand.plus.helpers.FontCache;
import net.osmand.plus.myplaces.AddNewTrackFolderBottomSheet;
import net.osmand.util.Algorithms;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SelectFavoriteCategoryBottomSheet extends MenuBottomSheetDialogFragment {

	public static final String TAG = SelectFavoriteCategoryBottomSheet.class.getSimpleName();
	private static final String SELECT_CATEGORY_EDITOR_TAG = "select_category_editor_tag";
	private static final String SELECT_CURRENT_CATEGORY_EDITOR_TAG = "select_current_category_editor_tag";
	private static String editorTag;
	private static String selectedCategory;
	private OsmandApplication app;
	private GPXUtilities.GPXFile gpxFile;
	private Map<String, PointsCategory> gpxCategories;
	private SelectFavoriteCategoryBottomSheet.CategorySelectionListener selectionListener;


	private static Drawable getIcon(final Activity activity, int resId, int color) {
		Drawable drawable = AppCompatResources.getDrawable(activity, resId);
		if (drawable != null) {
			drawable = DrawableCompat.wrap(drawable).mutate();
			drawable.clearColorFilter();
			drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
		}
		return drawable;
	}

	public static SelectFavoriteCategoryBottomSheet createInstance(String editorTag, String selectedCategory) {
		SelectFavoriteCategoryBottomSheet fragment = new SelectFavoriteCategoryBottomSheet();
		Bundle bundle = new Bundle();
		bundle.putString(SELECT_CATEGORY_EDITOR_TAG, editorTag);
		bundle.putString(SELECT_CURRENT_CATEGORY_EDITOR_TAG, selectedCategory);
		fragment.setArguments(bundle);
		fragment.setRetainInstance(true);
		return fragment;
	}

	public void setSelectionListener(SelectFavoriteCategoryBottomSheet.CategorySelectionListener selectionListener) {
		this.selectionListener = selectionListener;
	}

	public GPXUtilities.GPXFile getGpxFile() {
		return gpxFile;
	}

	public void setGpxFile(GPXUtilities.GPXFile gpxFile) {
		this.gpxFile = gpxFile;
	}

	public void setGpxCategories(Map<String, PointsCategory> gpxCategories) {
		this.gpxCategories = gpxCategories;
	}

	@Override
	public void createMenuItems(Bundle savedInstanceState) {
		app = requiredMyApplication();
		if (savedInstanceState != null) {
			restoreState(savedInstanceState);
		} else if (getArguments() != null) {
			restoreState(getArguments());
		}

		BaseBottomSheetItem titleItem = new BottomSheetItemWithDescription.Builder()
				.setDescription(getString(R.string.select_category_descr))
				.setTitle(getString(R.string.favorite_category_select))
				.setLayoutId(R.layout.bottom_sheet_item_title_with_description)
				.create();
		items.add(titleItem);
		final FragmentActivity activity = requireActivity();

		View addNewCategoryView = UiUtilities.getInflater(app, nightMode).inflate(R.layout.bottom_sheet_item_with_descr_64dp, null);
		addNewCategoryView.setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.bottom_sheet_list_item_height));
		TextView title = addNewCategoryView.findViewById(R.id.title);
		Typeface typeface = FontCache.getRobotoMedium(getContext());
		title.setTypeface(typeface);
		AndroidUiHelper.updateVisibility(addNewCategoryView.findViewById(R.id.description), false);
		BaseBottomSheetItem addNewFolderItem = new SimpleBottomSheetItem.Builder()
				.setTitle(getString(R.string.add_group))
				.setTitleColorId(ColorUtilities.getActiveColorId(nightMode))
				.setIcon(getActiveIcon(R.drawable.ic_action_folder_add))
				.setLayoutId(R.layout.bottom_sheet_item_with_descr_64dp)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						MapActivity mapActivity = (MapActivity) getActivity();
						Set<String> categories = gpxCategories != null ? gpxCategories.keySet() : null;
						AddNewFavoriteCategoryBottomSheet fragment = AddNewFavoriteCategoryBottomSheet.createInstance(editorTag, categories, gpxFile != null);
						if (mapActivity != null) {
							fragment.show(mapActivity.getSupportFragmentManager(), AddNewTrackFolderBottomSheet.class.getName());
						}
						fragment.setSelectionListener(selectionListener);
						dismiss();
					}
				})
				.setCustomView(addNewCategoryView)
				.create();
		items.add(addNewFolderItem);

		DividerItem dividerItem = new DividerItem(app);
		dividerItem.setMargins(0, 0, 0, 0);
		items.add(dividerItem);

		View favoriteCategoryList = UiUtilities.getInflater(app, nightMode).inflate(R.layout.favorite_categories_dialog, null);
		ScrollView scrollContainer = favoriteCategoryList.findViewById(R.id.scroll_container);
		scrollContainer.setPadding(0, 0, 0, 0);
		LinearLayout favoriteCategoryContainer = favoriteCategoryList.findViewById(R.id.list_container);

		final FavouritesDbHelper favoritesHelper = app.getFavorites();
		if (gpxFile != null) {
			if (gpxCategories != null) {
				Map<String, List<GPXUtilities.WptPt>> pointsByCategories = gpxFile.getPointsByCategories();
				for (Map.Entry<String, PointsCategory> entry : gpxCategories.entrySet()) {
					String categoryName = entry.getKey();
					PointsCategory category = entry.getValue();
					List<WptPt> categoryPoints = pointsByCategories.get(categoryName);
					String categorySize = Algorithms.isEmpty(categoryPoints)
							? app.getString(R.string.shared_string_empty)
							: String.valueOf(categoryPoints.size());
					favoriteCategoryContainer.addView(createCategoryItem(categoryName,
							category, categorySize, false));
				}
			}
		} else {
			List<FavouritesDbHelper.FavoriteGroup> gs = favoritesHelper.getFavoriteGroups();
			for (final FavouritesDbHelper.FavoriteGroup category : gs) {
				String favoriteCategoryCount;
				if (Algorithms.isEmpty(category.getPoints())) {
					favoriteCategoryCount = app.getString(R.string.shared_string_empty);
				} else {
					favoriteCategoryCount = String.valueOf(category.getPoints().size());
				}
				favoriteCategoryContainer.addView(createCategoryItem(category.getDisplayName(getContext()),
						category.toPointsCategory(), favoriteCategoryCount, !category.isVisible()));
			}
		}
		items.add(new BaseBottomSheetItem.Builder()
				.setCustomView(favoriteCategoryList)
				.create());
	}

	private View createCategoryItem(@NonNull String displayName, @NonNull PointsCategory category,
	                                @NonNull String categorySize, boolean isHidden) {
		Activity activity = requireActivity();
		View itemView = UiUtilities.getInflater(activity, nightMode)
				.inflate(R.layout.bottom_sheet_item_with_descr_and_radio_btn, null);
		final AppCompatImageView button = (AppCompatImageView) itemView.findViewById(R.id.icon);
		final int dp8 = AndroidUtils.dpToPx(app, 8f);
		final int dp16 = AndroidUtils.dpToPx(app, 16f);
		button.setPadding(0, 0, dp8, 0);
		LinearLayout descriptionContainer = itemView.findViewById(R.id.descriptionContainer);
		descriptionContainer.setPadding(dp16, 0, dp16, 0);
		itemView.setPadding(0, 0, 0, 0);
		int activeColorId = ColorUtilities.getActiveColorId(nightMode);

		if (isHidden) {
			button.setImageResource(R.drawable.ic_action_hide);
		} else {
			int displayColor = category.getColor() != 0
					? category.getColor()
					: ContextCompat.getColor(activity, gpxFile != null ? R.color.gpx_color_point : R.color.color_favorite);
			button.setImageDrawable(getIcon(activity, R.drawable.ic_action_folder, displayColor));
		}
		RadioButton compoundButton = itemView.findViewById(R.id.compound_button);
		compoundButton.setChecked(Algorithms.stringsEqual(selectedCategory, displayName));
		UiUtilities.setupCompoundButton(nightMode, ContextCompat.getColor(app,
				activeColorId), compoundButton);
		String name = displayName.length() == 0 ? getString(R.string.shared_string_favorites) : displayName;
		TextView text = itemView.findViewById(R.id.title);
		TextView description = itemView.findViewById(R.id.description);
		text.setText(name);
		description.setText(categorySize);
		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentActivity a = getActivity();
				if (a instanceof MapActivity) {
					PointEditor pointEditor = ((MapActivity) a).getContextMenu().getPointEditor(editorTag);
					if (pointEditor != null) {
						pointEditor.setCategory(category);
					}
					if (selectionListener != null) {
						selectionListener.onCategorySelected(category);
					}
				}
				dismiss();
			}
		});
		return itemView;
	}

	public void restoreState(Bundle bundle) {
		editorTag = bundle.getString(SELECT_CATEGORY_EDITOR_TAG);
		selectedCategory = bundle.getString(SELECT_CURRENT_CATEGORY_EDITOR_TAG);
	}

	public interface CategorySelectionListener {

		void onCategorySelected(@NonNull PointsCategory category);
	}
}