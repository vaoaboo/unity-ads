package com.unity3d.ads.android.campaign;

import java.io.File;
import java.util.ArrayList;

import android.util.Log;

import com.unity3d.ads.android.UnityAds;
import com.unity3d.ads.android.UnityAdsProperties;
import com.unity3d.ads.android.UnityAdsUtils;
import com.unity3d.ads.android.cache.UnityAdsDownloader;
import com.unity3d.ads.android.cache.IUnityAdsDownloadListener;

public class UnityAdsCampaignHandler implements IUnityAdsDownloadListener {
	
	private ArrayList<String> _downloadList = null;
	private UnityAdsCampaign _campaign = null;
	private ArrayList<UnityAdsCampaign> _activeCampaigns = null;
	private IUnityAdsCampaignHandlerListener _handlerListener = null;
	
	
	public UnityAdsCampaignHandler (UnityAdsCampaign campaign, ArrayList<UnityAdsCampaign> activeList) {
		_campaign = campaign;
		_activeCampaigns = activeList;
	}
	
	public boolean hasDownloads () {
		return (_downloadList != null && _downloadList.size() > 0);
	}

	public UnityAdsCampaign getCampaign () {
		return _campaign;
	}
	
	public void setListener (IUnityAdsCampaignHandlerListener listener) {
		_handlerListener = listener;
	}
	
	@Override
	public void onFileDownloadCompleted (String downloadUrl) {
		removeDownload(downloadUrl);
		
		if (_downloadList != null && _downloadList.size() == 0 && _handlerListener != null) {
			Log.d(UnityAdsProperties.LOG_NAME, "Reporting campaign download completion: " + _campaign.getCampaignId());
			UnityAdsDownloader.removeListener(this);
			_handlerListener.onCampaignHandled(this);
		}
	}
	
	@Override
	public void onFileDownloadCancelled (String downloadUrl) {		
	}
	
	public void initCampaign () {
		// Check video
		checkFileAndDownloadIfNeeded(_campaign.getVideoUrl());
		UnityAdsCampaign possiblyCachedCampaign = UnityAds.cachemanifest.getCachedCampaignById(_campaign.getCampaignId());
		
		// If manifest has this campaign and their files are not the same, remove the cached file if not needed anymore.
		if (possiblyCachedCampaign != null && !_campaign.getVideoUrl().equals(possiblyCachedCampaign.getVideoUrl()) && 
			!UnityAdsUtils.isFileRequiredByCampaigns(possiblyCachedCampaign.getVideoUrl(), _activeCampaigns))
			UnityAdsUtils.removeFile(possiblyCachedCampaign.getVideoUrl());
		
		// No downloads, report campaign done
		if (!hasDownloads() && _handlerListener != null)
			_handlerListener.onCampaignHandled(this);
	}
	
	
	/* INTERNAL METHODS */
	
	private void checkFileAndDownloadIfNeeded (String fileUrl) {
		if (!isFileCached(fileUrl)) {
			if (!hasDownloads())
				UnityAdsDownloader.addListener(this);
			
			addToFileDownloads(fileUrl);
		}
		else if (!isFileOk(fileUrl)) {
			UnityAdsUtils.removeFile(fileUrl);
			UnityAdsDownloader.addListener(this);
			addToFileDownloads(fileUrl);
		}		
	}
	
	private boolean isFileOk (String fileUrl) {
		// TODO: Implement isFileOk
		return true;
	}
	
	private void addToFileDownloads (String fileUrl) {
		if (fileUrl == null) return;
		if (_downloadList == null) _downloadList = new ArrayList<String>();
		
		_downloadList.add(fileUrl);
		UnityAdsDownloader.addDownload(fileUrl);
	}
	
	private boolean isFileCached (String fileName) {
		File targetFile = new File (fileName);
		File cachedFile = new File (UnityAdsUtils.getCacheDirectory() + "/" + targetFile.getName());
		
		return cachedFile.exists();
	}
	
	private void removeDownload (String downloadUrl) {
		if (_downloadList == null) return;
		
		int removeIndex = -1;
		
		for (int i = 0; i < _downloadList.size(); i++) {
			if (_downloadList.get(i).equals(downloadUrl)) {
				removeIndex = i;
				break;
			}
		}
		
		if (removeIndex > -1)
			_downloadList.remove(removeIndex);
	}
}
