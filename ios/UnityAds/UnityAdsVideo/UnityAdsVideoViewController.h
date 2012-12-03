//
//  UnityAdsVideoViewController.h
//  UnityAds
//
//  Created by bluesun on 11/26/12.
//  Copyright (c) 2012 Unity Technologies. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "UnityAdsVideoPlayer.h"
#import "../UnityAdsCampaign/UnityAdsCampaign.h"

@protocol UnityAdsVideoControllerDelegate <NSObject>

@required
- (void)videoPlayerStartedPlaying;
- (void)videoPlayerPlaybackEnded;
- (void)videoPlayerEncounteredError;
@end

@interface UnityAdsVideoViewController : UIViewController <UnityAdsVideoPlayerDelegate>
@property (nonatomic, assign) id<UnityAdsVideoControllerDelegate> delegate;
@property (nonatomic, assign) BOOL isPlaying;
- (void)playCampaign:(UnityAdsCampaign *)campaignToPlay;
- (void)forceStopVideoPlayer;
@end