/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.navigation

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Constraints
import com.twidere.twiderex.component.RequireAuthorization
import com.twidere.twiderex.component.navigation.LocalNavigator
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType
import com.twidere.twiderex.model.enums.MediaType
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.scenes.DraftListScene
import com.twidere.twiderex.scenes.HomeScene
import com.twidere.twiderex.scenes.PlatformPureMediaScene
import com.twidere.twiderex.scenes.PlatformRawMediaScene
import com.twidere.twiderex.scenes.PlatformStatusMediaScene
import com.twidere.twiderex.scenes.SignInScene
import com.twidere.twiderex.scenes.StatusScene
import com.twidere.twiderex.scenes.compose.ComposeScene
import com.twidere.twiderex.scenes.compose.ComposeSearchHashtagScene
import com.twidere.twiderex.scenes.compose.ComposeSearchUserScene
import com.twidere.twiderex.scenes.compose.DraftComposeScene
import com.twidere.twiderex.scenes.dm.DMConversationListScene
import com.twidere.twiderex.scenes.dm.DMConversationScene
import com.twidere.twiderex.scenes.dm.DMNewConversationScene
import com.twidere.twiderex.scenes.gif.GifScene
import com.twidere.twiderex.scenes.home.HomeTimelineScene
import com.twidere.twiderex.scenes.home.MeScene
import com.twidere.twiderex.scenes.home.MentionScene
import com.twidere.twiderex.scenes.home.mastodon.FederatedTimelineScene
import com.twidere.twiderex.scenes.home.mastodon.LocalTimelineScene
import com.twidere.twiderex.scenes.home.mastodon.MastodonNotificationScene
import com.twidere.twiderex.scenes.lists.ListTimeLineScene
import com.twidere.twiderex.scenes.lists.ListsAddMembersScene
import com.twidere.twiderex.scenes.lists.ListsMembersScene
import com.twidere.twiderex.scenes.lists.ListsScene
import com.twidere.twiderex.scenes.lists.ListsSubscribersScene
import com.twidere.twiderex.scenes.lists.platform.MastodonListsCreateDialog
import com.twidere.twiderex.scenes.lists.platform.TwitterListsCreateScene
import com.twidere.twiderex.scenes.lists.platform.TwitterListsEditScene
import com.twidere.twiderex.scenes.mastodon.MastodonHashtagScene
import com.twidere.twiderex.scenes.mastodon.MastodonSignInScene
import com.twidere.twiderex.scenes.search.SearchInputScene
import com.twidere.twiderex.scenes.search.SearchScene
import com.twidere.twiderex.scenes.search.fadeCreateTransition
import com.twidere.twiderex.scenes.search.fadeDestroyTransition
import com.twidere.twiderex.scenes.search.fadePauseTransition
import com.twidere.twiderex.scenes.search.fadeResumeTransition
import com.twidere.twiderex.scenes.settings.AboutScene
import com.twidere.twiderex.scenes.settings.AccountManagementScene
import com.twidere.twiderex.scenes.settings.AccountNotificationScene
import com.twidere.twiderex.scenes.settings.AppearanceScene
import com.twidere.twiderex.scenes.settings.DisplayScene
import com.twidere.twiderex.scenes.settings.LayoutScene
import com.twidere.twiderex.scenes.settings.MiscScene
import com.twidere.twiderex.scenes.settings.NotificationScene
import com.twidere.twiderex.scenes.settings.SettingsScene
import com.twidere.twiderex.scenes.settings.StorageScene
import com.twidere.twiderex.scenes.twitter.TwitterSignInScene
import com.twidere.twiderex.scenes.twitter.user.TwitterUserScene
import com.twidere.twiderex.scenes.user.FollowersScene
import com.twidere.twiderex.scenes.user.FollowingScene
import com.twidere.twiderex.scenes.user.UserScene
import com.twidere.twiderex.twitterHosts
import com.twidere.twiderex.ui.LocalActiveAccount
import com.twidere.twiderex.ui.LocalActiveAccountViewModel
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.utils.LocalPlatformResolver
import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.query
import moe.tlaster.precompose.navigation.transition.NavTransition
import moe.tlaster.precompose.navigation.transition.fadeScaleCreateTransition
import moe.tlaster.precompose.navigation.transition.fadeScaleDestroyTransition
import java.net.URLDecoder

const val initialRoute = RootRouteDefinition.Home

fun RouteBuilder.authorizedScene(
    route: String,
    deepLinks: List<String> = emptyList(),
    navTransition: NavTransition? = null,
    content: @Composable (BackStackEntry) -> Unit,
) {
    scene(route, deepLinks, navTransition) {
        RequireAuthorization {
            content.invoke(it)
        }
    }
}

fun RouteBuilder.authorizedDialog(
    route: String,
    content: @Composable (BackStackEntry) -> Unit,
) {
    dialog(route) {
        RequireAuthorization {
            content.invoke(it)
        }
    }
}

@Composable
fun ProvidePlatformType(
    key: MicroBlogKey,
    provider: suspend () -> PlatformType?,
    content: @Composable (platformType: PlatformType) -> Unit,
) {
    var platformType by rememberSaveable {
        mutableStateOf<PlatformType?>(null)
    }
    val account = LocalActiveAccount.current
    LaunchedEffect(key) {
        platformType = provider.invoke() ?: account?.type
    }
    platformType?.let {
        content.invoke(it)
    } ?: run {
        TwidereScene {
            Scaffold {
            }
        }
    }
}

@Composable
fun ProvideStatusPlatform(
    statusKey: MicroBlogKey,
    content: @Composable (platformType: PlatformType) -> Unit,
) {
    val platformResolver = LocalPlatformResolver.current
    val account = LocalActiveAccount.current ?: return
    ProvidePlatformType(
        key = statusKey,
        provider = {
            platformResolver.resolveStatus(statusKey = statusKey, account.accountKey)
        },
        content = content
    )
}

@Composable
fun ProvideUserPlatform(
    userKey: MicroBlogKey,
    content: @Composable (platformType: PlatformType) -> Unit,
) {
    val platformResolver = LocalPlatformResolver.current
    ProvidePlatformType(
        key = userKey,
        provider = {
            platformResolver.resolveUser(userKey = userKey)
        },
        content = content
    )
}

@Composable
fun RequirePlatformAccount(
    platformType: PlatformType,
    fallback: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    var account = LocalActiveAccount.current ?: run {
        fallback.invoke()
        return
    }
    if (account.type != platformType) {
        account = LocalActiveAccountViewModel.current.getTargetPlatformDefault(platformType)
            ?: run {
                fallback.invoke()
                return
            }
    }
    CompositionLocalProvider(
        LocalActiveAccount provides account
    ) {
        content.invoke()
    }
}

/**
 * Note: Path/Query key must be exactly the same as function's parameter's name in Root/RootDeepLinks
 */
fun RouteBuilder.route(constraints: Constraints) {
    authorizedScene(
        RootRouteDefinition.Home,
        deepLinks = twitterHosts.map { "$it/*" }
    ) {
        HomeScene()
    }

    authorizedScene(RootRouteDefinition.Mastodon.Notification) {
        MastodonNotificationScene()
    }

    authorizedScene(RootRouteDefinition.Mastodon.FederatedTimeline) {
        FederatedTimelineScene()
    }

    authorizedScene(RootRouteDefinition.Mastodon.LocalTimeline) {
        LocalTimelineScene()
    }

    authorizedScene(RootRouteDefinition.Me) {
        MeScene()
    }

    authorizedScene(RootRouteDefinition.Mentions) {
        MentionScene()
    }

    authorizedScene(RootRouteDefinition.HomeTimeline) {
        HomeTimelineScene()
    }

    scene(
        RootRouteDefinition.SignIn.General,
        deepLinks = listOf(
            RootDeepLinksRouteDefinition.SignIn
        ),
    ) {
        SignInScene()
    }

    scene(
        RootRouteDefinition.SignIn.Twitter,
    ) { backStackEntry ->
        val consumerKey = backStackEntry.path<String>("consumerKey")
        val consumerSecret = backStackEntry.path<String>("consumerSecret")
        if (consumerKey != null && consumerSecret != null) {
            TwitterSignInScene(consumerKey = consumerKey, consumerSecret = consumerSecret)
        }
    }

    scene(RootRouteDefinition.SignIn.Mastodon) {
        MastodonSignInScene()
    }

    // scene(
    //     "signin/mastodon/web/{target}",
    // ) { backStackEntry ->
    //     backStackEntry.path<String>("target")?.let {
    //         MastodonWebSignInScene(target = URLDecoder.decode(it, "UTF-8"))
    //     }
    // }

    authorizedScene(
        RootDeepLinksRouteDefinition.Twitter.User,
        deepLinks = twitterHosts.map {
            "$it/{screenName}"
        }
    ) { backStackEntry ->
        backStackEntry.path<String>("screenName")?.let { screenName ->
            val navigator = LocalNavigator.current
            RequirePlatformAccount(
                platformType = PlatformType.Twitter,
                fallback = {
                    navigator.openLink("https://twitter.com/$screenName", deepLink = false)
                    navigator.goBack()
                }
            ) {
                TwitterUserScene(screenName = screenName)
            }
        }
    }

    authorizedScene(
        RootRouteDefinition.User,
        deepLinks = listOf(
            RootDeepLinksRouteDefinition.User
        )
    ) { backStackEntry ->
        backStackEntry.path<String>("userKey")?.let {
            MicroBlogKey.valueOf(it)
        }?.let { userKey ->
            ProvideUserPlatform(userKey = userKey) { platformType ->
                RequirePlatformAccount(platformType = platformType) {
                    UserScene(userKey)
                }
            }
        }
    }

    authorizedScene(
        RootRouteDefinition.Mastodon.Hashtag,
        deepLinks = listOf(
            RootDeepLinksRouteDefinition.Mastodon.Hashtag
        )
    ) { backStackEntry ->
        backStackEntry.path<String>("keyword")?.let {
            MastodonHashtagScene(keyword = it)
        }
    }

    authorizedScene(
        RootRouteDefinition.Status,
        deepLinks = listOf(
            RootDeepLinksRouteDefinition.Status,
        )
    ) { backStackEntry ->
        backStackEntry.path<String>("statusKey")?.let {
            MicroBlogKey.valueOf(it)
        }?.let { statusKey ->
            ProvideStatusPlatform(statusKey = statusKey) { platform ->
                RequirePlatformAccount(platformType = platform) {
                    StatusScene(statusKey = statusKey)
                }
            }
        }
    }

    authorizedScene(
        RootDeepLinksRouteDefinition.Twitter.Status,
        deepLinks = twitterHosts.map {
            "$it/{screenName}/status/{statusId:[0-9]+}"
        }
    ) { backStackEntry ->
        backStackEntry.path<String>("statusId")?.let { statusId ->
            val navigator = LocalNavigator.current
            RequirePlatformAccount(
                platformType = PlatformType.Twitter,
                fallback = {
                    navigator.openLink(
                        "https://twitter.com/${backStackEntry.path<String>("screenName")}/status/$statusId",
                        deepLink = false
                    )
                    navigator.goBack()
                }
            ) {
                StatusScene(statusKey = MicroBlogKey.twitter(statusId))
            }
        }
    }

    authorizedDialog(
        RootRouteDefinition.Media.Status,
    ) { backStackEntry ->
        backStackEntry.path<String>("statusKey")?.let {
            MicroBlogKey.valueOf(it)
        }?.let { statusKey ->
            ProvideStatusPlatform(statusKey = statusKey) { platformType ->
                RequirePlatformAccount(platformType = platformType) {
                    val selectedIndex = backStackEntry.query("selectedIndex", 0) ?: 0
                    PlatformStatusMediaScene(
                        statusKey = statusKey,
                        selectedIndex = selectedIndex
                    )
                }
            }
        }
    }

    authorizedDialog(
        RootRouteDefinition.Media.Pure,
    ) { backStackEntry ->
        backStackEntry.path<String>("belongToKey")?.let {
            MicroBlogKey.valueOf(it)
        }?.let { belongToKey ->
            val selectedIndex = backStackEntry.query("selectedIndex", 0) ?: 0
            PlatformPureMediaScene(
                belongToKey = belongToKey,
                selectedIndex = selectedIndex
            )
        }
    }

    authorizedDialog(
        RootRouteDefinition.Media.Raw,
    ) { backStackEntry ->
        val url = backStackEntry.path<String>("url")?.let {
            URLDecoder.decode(it, "UTF-8")
        }
        val type = MediaType.valueOf(backStackEntry.path<String>("type") ?: MediaType.photo.name)
        url?.let {
            PlatformRawMediaScene(url = it, type = type)
        }
    }

    authorizedScene(RootRouteDefinition.Search.Home) {
        com.twidere.twiderex.scenes.home.SearchScene()
    }

    authorizedScene(
        RootRouteDefinition.Search.Input,
        navTransition = NavTransition(
            createTransition = fadeCreateTransition,
            destroyTransition = fadeDestroyTransition,
            pauseTransition = fadePauseTransition,
            resumeTransition = fadeResumeTransition,
        ),
    ) { backStackEntry ->
        SearchInputScene(
            backStackEntry.query<String>("keyword")?.let { URLDecoder.decode(it, "UTF-8") }
        )
    }

    authorizedScene(
        RootRouteDefinition.Search.Result,
        deepLinks = twitterHosts.map {
            "$it/search?q={keyword}"
        } + RootDeepLinksRouteDefinition.Search,
        navTransition = NavTransition(
            createTransition = fadeCreateTransition,
            destroyTransition = fadeDestroyTransition,
            pauseTransition = fadePauseTransition,
            resumeTransition = fadeResumeTransition,
        ),
    ) { backStackEntry ->
        backStackEntry.path<String>("keyword")?.takeIf { it.isNotEmpty() }?.let {
            SearchScene(keyword = URLDecoder.decode(it, "UTF-8"))
        }
    }

    authorizedScene(
        RootRouteDefinition.Compose.Home,
        navTransition = NavTransition(
            createTransition = {
                translationY = constraints.maxHeight * (1 - it)
                alpha = it
            },
            destroyTransition = {
                translationY = constraints.maxHeight * (1 - it)
                alpha = it
            },
            pauseTransition = fadeScaleDestroyTransition,
            resumeTransition = fadeScaleCreateTransition,
        ),
        deepLinks = listOf(
            RootDeepLinksRouteDefinition.Compose
        )
    ) { backStackEntry ->
        val type = backStackEntry.query<String>("composeType")?.let {
            enumValueOf(it)
        } ?: ComposeType.New
        val statusKey = backStackEntry.query<String>("statusKey")
            ?.takeIf { it.isNotEmpty() }
            ?.let { MicroBlogKey.valueOf(it) }
        if (statusKey != null) {
            ProvideStatusPlatform(statusKey = statusKey) { platformType ->
                RequirePlatformAccount(platformType = platformType) {
                    ComposeScene(statusKey, type)
                }
            }
        } else {
            ComposeScene(statusKey, type)
        }
    }

    authorizedScene(
        RootRouteDefinition.Followers,
    ) { backStackEntry ->
        backStackEntry.path<String>("userKey")?.let {
            MicroBlogKey.valueOf(it)
        }?.let {
            FollowersScene(it)
        }
    }

    authorizedScene(
        RootRouteDefinition.Following,
    ) { backStackEntry ->
        backStackEntry.path<String>("userKey")?.let {
            MicroBlogKey.valueOf(it)
        }?.let {
            FollowingScene(it)
        }
    }

    scene(RootRouteDefinition.Settings.Home) {
        SettingsScene()
    }

    scene(RootRouteDefinition.Settings.Appearance) {
        AppearanceScene()
    }

    scene(RootRouteDefinition.Settings.Display) {
        DisplayScene()
    }

    scene(RootRouteDefinition.Settings.Storage) {
        StorageScene()
    }

    scene(RootRouteDefinition.Settings.AccountManagement) {
        AccountManagementScene()
    }

    scene(RootRouteDefinition.Settings.Misc) {
        MiscScene()
    }

    scene(RootRouteDefinition.Settings.Notification) {
        NotificationScene()
    }

    authorizedScene(RootRouteDefinition.Settings.Layout) {
        LayoutScene()
    }

    scene(
        RootRouteDefinition.Settings.AccountNotification
    ) {
        it.path<String>("accountKey", null)?.let {
            MicroBlogKey.valueOf(it)
        }?.let {
            AccountNotificationScene(it)
        }
    }

    scene(RootRouteDefinition.Settings.About) {
        AboutScene()
    }

    scene(RootRouteDefinition.Draft.List) {
        DraftListScene()
    }

    scene(
        RootRouteDefinition.Draft.Compose,
        deepLinks = listOf(
            RootDeepLinksRouteDefinition.Draft,
        )
    ) { backStackEntry ->
        backStackEntry.path<String>("draftId")?.let {
            DraftComposeScene(draftId = it)
        }
    }

    authorizedScene(RootRouteDefinition.Compose.Search.User) {
        ComposeSearchUserScene()
    }

    authorizedScene(RootRouteDefinition.Mastodon.Compose.Hashtag) {
        ComposeSearchHashtagScene()
    }

    authorizedScene(RootRouteDefinition.Lists.Home) {
        ListsScene()
    }

    authorizedDialog(RootRouteDefinition.Lists.MastodonCreateDialog) {
        val navController = LocalNavController.current
        MastodonListsCreateDialog(onDismissRequest = { navController.goBack() })
    }

    authorizedScene(RootRouteDefinition.Lists.TwitterCreate) {
        TwitterListsCreateScene()
    }

    authorizedScene(
        RootRouteDefinition.Lists.TwitterEdit,
    ) { backStackEntry ->
        backStackEntry.path<String>("listKey")?.let {
            TwitterListsEditScene(listKey = MicroBlogKey.valueOf(it))
        }
    }

    authorizedScene(
        RootRouteDefinition.Lists.Timeline,
    ) { backStackEntry ->
        backStackEntry.path<String>("listKey")?.let {
            ListTimeLineScene(listKey = MicroBlogKey.valueOf(it))
        }
    }

    authorizedScene(
        RootRouteDefinition.Lists.Members,
    ) { backStackEntry ->
        backStackEntry.path<String>("listKey")?.let {
            ListsMembersScene(listKey = MicroBlogKey.valueOf(it), backStackEntry.query<Boolean>("owned") ?: false)
        }
    }

    authorizedScene(
        RootRouteDefinition.Lists.Subscribers,
    ) { backStackEntry ->
        backStackEntry.path<String>("listKey")?.let {
            ListsSubscribersScene(listKey = MicroBlogKey.valueOf(it))
        }
    }

    authorizedScene(
        RootRouteDefinition.Lists.AddMembers,
    ) { backStackEntry ->
        backStackEntry.path<String>("listKey")?.let {
            ListsAddMembersScene(listKey = MicroBlogKey.valueOf(it))
        }
    }

    authorizedScene(RootRouteDefinition.Messages.Home) {
        DMConversationListScene()
    }

    authorizedScene(RootRouteDefinition.Messages.NewConversation) {
        DMNewConversationScene()
    }

    authorizedScene(
        RootRouteDefinition.Messages.Conversation,
        deepLinks = listOf(
            RootDeepLinksRouteDefinition.Conversation
        )
    ) { backStackEntry ->
        backStackEntry.path<String>("conversationKey")?.let {
            DMConversationScene(conversationKey = MicroBlogKey.valueOf(it))
        }
    }

    authorizedScene(RootRouteDefinition.Gif.Home) {
        GifScene()
    }

    platformScene()
}

expect fun RouteBuilder.platformScene()
