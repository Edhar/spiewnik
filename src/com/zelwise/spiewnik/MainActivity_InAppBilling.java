package com.zelwise.spiewnik;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.zelwise.spiewnik.iab.IabHelper;
import com.zelwise.spiewnik.iab.IabResult;
import com.zelwise.spiewnik.iab.Purchase;

public class MainActivity_InAppBilling extends MainActivity_Ext {
	public MainActivity_InAppBilling(MainActivity mainActivity) {
		super(mainActivity);
	}

	// licensing
	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjq+xQZIbUBTxgN0k22D78UFyqLVOkYJw8LJI/VoCl9aaD0fRm67L+a9zc2wIXrHVZBeki7gLLZ+eVO322kIbw37ebPjlc2GeRk5MdqA6+94cmQEXk8XPjOuQQki/iqEV4n6O42OW7MioE3dn4eRW5w6Xh6QDzxYAbAlMBvwlSfLDW409G10Bke3wfm8cjSnQ99PKPL5ClR5h4fPRd9frMlwcXPAVLueO8qsV8WefH7cmMdXbbijndjIiaPM9/0NO7RdkufWU51slLLqpD5mOC2D8nS99nDvrhdu9FK/gVyxnVBRgVebRyiXT99/E20aaEaEmEpGBNAO2rqzXdSFZ1QIDAQAB";

	// Generate your own 20 random bytes, and put them here.
	private static final byte[] SALT = new byte[] { 86, 45, 30, 86, -13, -57, 74, -45, 51, 98, -95, -45, 45, 45, 45, -6, 78, 32, -64, 86 };
	// licensing

	IabHelper mHelper;

	// important : put your own SKU's as defined here
	static final String SKU_SMALL = "donate_small_prod";
	static final String SKU_MEDIUM = "donate_medium_prod";
	static final String SKU_LARGE = "donate_large_prod";
	static final String SKU_XL = "donate_xl_prod";
	static final String SKU_XXL = "donate_xxl_prod";
	
	public ArrayList<DonateProduct> GetDonateProducts() {
		ArrayList<DonateProduct> list = new ArrayList<DonateProduct>();
		list.add(new DonateProduct(1, "1 $", SKU_SMALL));
		list.add(new DonateProduct(1, "2 $", SKU_MEDIUM));
		list.add(new DonateProduct(1, "3 $", SKU_LARGE));
		list.add(new DonateProduct(1, "5 $", SKU_XL));
		list.add(new DonateProduct(1, "10 $", SKU_XXL));
		return list;
	}
	
	
	// (arbitrary) request code for the purchase flow
	static final int RC_REQUEST = 10001;

	private void toast(String text) {
		Toast.makeText(MainAct, text, Toast.LENGTH_SHORT).show();
	}

	private void toast(int stringRecouseId, String additionalText) {
		toast(MainAct.getResources().getString(stringRecouseId) + " " + additionalText);
	}

	private void toast(int stringRecouseId) {
		toast(stringRecouseId, "");
	}

	// Called when consumption is complete
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {

		public void onConsumeFinished(Purchase purchase, IabResult result) {
			// if we were disposed of in the meantime, quit.

			if (mHelper == null) {
				return;
			}

			// check which SKU is consumed here and then proceed.
			if (result.isSuccess()) {
				toast(R.string.iab_ConsumeFinished_ThankYou);

			} else {
				toast(R.string.iab_ConsumeFinished_error, result.toString());
			}

		}
	};

	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();

		/**
		 * Follow google guidelines to create your own payload string here, in
		 * case it is needed. Remember it is recommended to store the keys on
		 * your own server for added protection USE as necessary
		 */

		return true;
	}

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {

		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			// if we were disposed of in the meantime, quit.

			if (mHelper == null) {
				return;
			}

			if (result.isFailure()) {
				toast(R.string.iab_purchase_error, result.toString());
				// setWaitScreen(false);
				return;

			}

			if (!verifyDeveloperPayload(purchase)) {
				toast(R.string.iab_verifyDeveloperPayload_error);

				// setWaitScreen(false);

				return;

			}

			if (purchase.getSku().equals(SKU_SMALL)

			|| purchase.getSku().equals(SKU_MEDIUM)

			|| purchase.getSku().equals(SKU_LARGE)

			|| purchase.getSku().equals(SKU_XL)

			|| purchase.getSku().equals(SKU_XXL)) {

				// check if any item is consumed

				mHelper.consumeAsync(purchase, mConsumeFinishedListener);

			}

		}

	};

	public void onDestroy() {
		if (mHelper != null) {
			mHelper.dispose();
			mHelper = null;
		}
	}

	// the button clicks send an int value which would then call the specific
	// SKU, depending on the
	// application
	public void makeDonation(DonateProduct product) {
		// check your own payload string.
		String payload = "";
		mHelper.launchPurchaseFlow(MainAct, product.ProductSKU(), RC_REQUEST, mPurchaseFinishedListener, payload);
	}

	@Override
	public void onCreate() {
		// code

		String base64EncodedPublicKey = BASE64_PUBLIC_KEY;

		mHelper = new IabHelper(MainAct, base64EncodedPublicKey);

		// enable debug logging (for a production application, you should set
		// this to false).
		mHelper.enableDebugLogging(false);

		// Start setup. This is asynchronous and the specified listener will be
		// called once setup completes.
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					toast(R.string.iab_bill_error, result.toString());
					return;
				}

				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null)
					return;

				// IAB is fully set up. Now, let's get an inventory of stuff we
				// own.

				// --commented out here as we didn't need it for donation
				// purposes.
				// mHelper.queryInventoryAsync(mGotInventoryListener);

			}

		});

	}

}
