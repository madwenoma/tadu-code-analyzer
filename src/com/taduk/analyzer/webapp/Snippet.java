package com.taduk.analyzer.webapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class Snippet {
	static Map<String, String> map = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put(" ActivityFacade", " FacadeRepository.activityFacade"); 
			put(" AdvertisingFacade", " FacadeRepository.advertisingFacade"); 
			put(" AndroidProductFacade", " FacadeRepository.androidProductFacade"); 
			put(" ApplicationExtensionFacade", " FacadeRepository.applicationExtensionFacade"); 
			put(" ArticleFacade", " FacadeRepository.articleFacade"); 
			put(" AuthorFacade", " FacadeRepository.authorFacade"); 
			put(" AuthorSayingFacade", " FacadeRepository.authorSayingFacade"); 
			put(" AwardFacade", " FacadeRepository.awardFacade"); 
			put(" BlackListFacade", " FacadeRepository.blackListFacade"); 
			put(" BookCategoryFacade", " FacadeRepository.bookCategoryFacade"); 
			put(" BookChannelFacade", " FacadeRepository.bookChannelFacade"); 
			put(" BookChargeFacade", " FacadeRepository.bookChargeFacade"); 
			put(" BookDigestCommentFacade", " FacadeRepository.bookDigestCommentFacade"); 
			put(" BookDigestFacade", " FacadeRepository.bookDigestFacade"); 
			put(" BookFacade", " FacadeRepository.bookFacade"); 
			put(" BookInfo2HezuoFacade", " FacadeRepository.bookInfo2HezuoFacade"); 
			put(" BookLabelFacade", " FacadeRepository.bookLabelFacade"); 
			put(" BookLabelWeightFacade", " FacadeRepository.bookLabelWeightFacade"); 
			put(" BooklistFacade", " FacadeRepository.booklistFacade"); 
			put(" BooklistLabelFacade", " FacadeRepository.booklistLabelFacade"); 
			put(" BookmarkFacade", " FacadeRepository.bookmarkFacade"); 
			put(" BookPageFacade", " FacadeRepository.bookPageFacade"); 
			put(" BookPartFacade", " FacadeRepository.bookPartFacade"); 
			put(" BookRankFacade", " FacadeRepository.bookRankFacade"); 
			put(" BookRemarkFacade", " FacadeRepository.bookRemarkFacade"); 
			put(" BookTopicFacade", " FacadeRepository.bookTopicFacade"); 
			put(" BookZoneFacade", " FacadeRepository.bookZoneFacade"); 
			put(" BrushFacade", " FacadeRepository.brushFacade"); 
			put(" CaptchastoreFacade", " FacadeRepository.captchastoreFacade"); 
			put(" CarouselFacade", " FacadeRepository.carouselFacade"); 
			put(" ChargeFacade", " FacadeRepository.chargeFacade"); 
			put(" ChargeFacadeRefactor", " FacadeRepository.chargeFacadeRefactor"); 
			put(" CommentFacade", " FacadeRepository.commentFacade"); 
			put(" CopyrightTypeFacade", " FacadeRepository.copyrightTypeFacade"); 
			put(" CouponFacade", " FacadeRepository.couponFacade"); 
			put(" CrossBookshelfFacade", " FacadeRepository.crossBookshelfFacade"); 
			put(" DiscountChapterFacade", " FacadeRepository.discountChapterFacade"); 
			put(" DuoMiFacade", " FacadeRepository.duoMiFacade"); 
			put(" FavoriteFacade", " FacadeRepository.favoriteFacade"); 
			put(" FeedbackFacade", " FacadeRepository.feedbackFacade"); 
			put(" FlashSaleFacade", " FacadeRepository.flashSaleFacade"); 
			put(" FolderFacade", " FacadeRepository.folderFacade"); 
			put(" FontFacade", " FacadeRepository.fontFacade"); 
			put(" GiftFacade", " FacadeRepository.giftFacade"); 
			put(" HistoryFacade", " FacadeRepository.historyFacade"); 
			put(" HotwordFacade", " FacadeRepository.hotwordFacade"); 
			put(" IosProductFacade", " FacadeRepository.iosProductFacade"); 
			put(" IosProductVipFacade", " FacadeRepository.iosProductVipFacade"); 
			put(" IosProductWordFacade", " FacadeRepository.iosProductWordFacade"); 
			put(" IosSingleBookFacade", " FacadeRepository.iosSingleBookFacade"); 
			put(" IosUserWordFacade", " FacadeRepository.iosUserWordFacade"); 
			put(" IosVipQualificationFacade", " FacadeRepository.iosVipQualificationFacade"); 
			put(" IosVoucherFacade", " FacadeRepository.iosVoucherFacade"); 
			put(" IosVoucherVipFacade", " FacadeRepository.iosVoucherVipFacade"); 
			put(" LikeRelationFacade", " FacadeRepository.likeRelationFacade"); 
			put(" LmDataFacade", " FacadeRepository.lmDataFacade"); 
			put(" LoadingPicFacade", " FacadeRepository.loadingPicFacade"); 
			put(" LogoFacade", " FacadeRepository.logoFacade"); 
			put(" LotteryActivityFacade", " FacadeRepository.lotteryActivityFacade"); 
			put(" LotteryFacade", " FacadeRepository.lotteryFacade"); 
			put(" LotteryRecordFacade", " FacadeRepository.lotteryRecordFacade"); 
			put(" LotteryUserFacade", " FacadeRepository.lotteryUserFacade"); 
			put(" MarketingSlotFacade", " FacadeRepository.marketingSlotFacade"); 
			put(" MedalFacade", " FacadeRepository.medalFacade"); 
			put(" MessageFacade", " FacadeRepository.messageFacade"); 
			put(" ModelListFacade", " FacadeRepository.modelListFacade"); 
			put(" OperateData2HezuoFacade", " FacadeRepository.operateData2HezuoFacade"); 
			put(" OperationFacade", " FacadeRepository.operationFacade"); 
			put(" OrderFacade", " FacadeRepository.orderFacade"); 
			put(" OriginaBookFacade", " FacadeRepository.originaBookFacade"); 
			put(" PhysicalLotteryRecordFacade", " FacadeRepository.physicalLotteryRecordFacade"); 
			put(" PlatformConfigFacade", " FacadeRepository.platformConfigFacade"); 
			put(" PlatformFacade", " FacadeRepository.platformFacade"); 
			put(" PlatformGroupBookFacade", " FacadeRepository.platformGroupBookFacade"); 
			put(" PushFacade", " FacadeRepository.pushFacade"); 
			put(" RechargeFacade", " FacadeRepository.rechargeFacade"); 
			put(" RechargeVipInfoFacade", " FacadeRepository.rechargeVipInfoFacade"); 
			put(" RechargeWordInfoFacade", " FacadeRepository.rechargeWordInfoFacade"); 
			put(" RecommendFacade", " FacadeRepository.recommendFacade"); 
			put(" RefreshCacheFacade", " FacadeRepository.refreshCacheFacade"); 
			put(" ReportBookRecommendFacade", " FacadeRepository.reportBookRecommendFacade"); 
			put(" ResetPasswordFacade", " FacadeRepository.resetPasswordFacade"); 
			put(" SingleBook360SizeFacade", " FacadeRepository.singleBook360SizeFacade"); 
			put(" SkinFacade", " FacadeRepository.skinFacade"); 
			put(" SmsInfoAddressFacade", " FacadeRepository.smsInfoAddressFacade"); 
			put(" SoftwareUpdateFacade", " FacadeRepository.softwareUpdateFacade"); 
			put(" SubscribeFacade", " FacadeRepository.subscribeFacade"); 
			put(" SubscribeQueryFacade", " FacadeRepository.subscribeQueryFacade"); 
			put(" TipFacade", " FacadeRepository.tipFacade"); 
			put(" TopicRankBookFacade", " FacadeRepository.topicRankBookFacade"); 
			put(" TopicRankFacade", " FacadeRepository.topicRankFacade"); 
			put(" UserBlacklistFacade", " FacadeRepository.userBlacklistFacade"); 
			put(" UserChargedPhoneFacade", " FacadeRepository.userChargedPhoneFacade"); 
			put(" UserGradeConfFacade", " FacadeRepository.userGradeConfFacade"); 
			put(" UserMessageFacade", " FacadeRepository.userMessageFacade"); 
			put(" UserMoodFacade", " FacadeRepository.userMoodFacade"); 
			put(" UserProfileFacade", " FacadeRepository.userProfileFacade"); 
			put(" UserSkinFacade", " FacadeRepository.userSkinFacade"); 
			put(" UserVersionFacade", " FacadeRepository.userVersionFacade"); 
			put(" VirtualMoneyFacade", " FacadeRepository.virtualMoneyFacade"); 
			put(" VirtualTeamFacade", " FacadeRepository.virtualTeamFacade"); 
			put(" WebAdvertisermentInfoFacade", " FacadeRepository.webAdvertisermentInfoFacade"); 
			put(" WebAuthorInterviewFacade", " FacadeRepository.webAuthorInterviewFacade"); 
			put(" WebAuthorInterviewInfoFacade", " FacadeRepository.webAuthorInterviewInfoFacade"); 
			put(" WebIndexBookFacade", " FacadeRepository.webIndexBookFacade"); 
			put(" WebNewsInfoFacade", " FacadeRepository.webNewsInfoFacade"); 
			put(" WebSpeakerInfoFacade", " FacadeRepository.webSpeakerInfoFacade"); 
			put(" YrtTradeFacade", " FacadeRepository.yrtTradeFacade"); 
		}
	};

	public static void main(String[] args) throws IOException {
		Collection<File> files = FileUtils
				.listFiles(
						new File(
								"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\tadu-dubbo-android\\src"),
						new String[] { "java" }, true);

		for (File file : files) {
			if (file.exists() && file.getName().contains(".java")) {
				List<String> fileLines = FileUtils.readLines(file);
				List<String> newLines = new ArrayList<String>(fileLines.size());
				boolean needRewrite = false;
				for (String line : fileLines) {
					newLines.add(line);
					Set<Entry<String, String>> en = map.entrySet();
					for (Entry<String, String> entry : en) {
						// String [] lineStrs = line.split(" ");
						// if (Arrays.asList(lineStrs).contains(entry.getKey()))
						// {
						if (line.contains(entry.getKey())) {
							newLines.remove(line);
							line = line.replaceAll(entry.getKey(), entry.getValue());
							newLines.add(line);
							needRewrite = true;
						}
					}
				}
				if (needRewrite) {
					FileUtils.writeLines(file, newLines);
				}
			}
		}
		
		
	}
}
