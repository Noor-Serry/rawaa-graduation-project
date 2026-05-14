package noor.serry.rawaa.ui.screens.studentScreens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.navigation.student.StudentBackStackProvider

// ─────────────────────────────────────────────────────────────────────────────
// Data model
// ─────────────────────────────────────────────────────────────────────────────

private data class PolicySection(
    val icon: Int,
    val title: String,
    val body: String,
)

private val policySections = listOf(
    PolicySection(
        icon  = R.drawable.shield,
        title = "جمع المعلومات",
        body  = "نقوم بجمع المعلومات التي تقدمها مباشرةً عند إنشاء حساب أو استخدام منصتنا، وتشمل: الاسم الكامل، والبريد الإلكتروني، ورقم الهاتف، والمعلومات الأكاديمية كالقسم والمستوى الدراسي. لا نجمع أي معلومات شخصية إضافية دون موافقتك الصريحة.",
    ),
    PolicySection(
        icon  = R.drawable.ic_book,
        title = "استخدام المعلومات",
        body  = "تُستخدم بياناتك حصرياً لتشغيل الخدمات التعليمية وتحسينها، ولإرسال الإشعارات المتعلقة بالجداول الدراسية والامتحانات ونتائج الدرجات، وللتواصل معك بشأن تحديثات الحساب. لن يتم استخدام بياناتك لأي أغراض تجارية أو إعلانية.",
    ),
    PolicySection(
        icon  = R.drawable.ic_person,
        title = "مشاركة المعلومات",
        body  = "نلتزم بعدم بيع أي معلومات شخصية لأطراف ثالثة أو مشاركتها معهم. قد نشارك بيانات محدودة مع أعضاء هيئة التدريس والإدارة الأكاديمية المرخص لهم، وذلك ضمن نطاق المؤسسة التعليمية فقط وبما يخدم مصلحتك الأكاديمية.",
    ),
    PolicySection(
        icon  = R.drawable.ic_clock,
        title = "الاحتفاظ بالبيانات",
        body  = "نحتفظ بسجلاتك الأكاديمية طوال فترة دراستك في المؤسسة، وبعد التخرج لفترة لا تتجاوز خمس سنوات وفقاً للمتطلبات القانونية. يحق لك في أي وقت طلب حذف بياناتك الشخصية غير الأكاديمية.",
    ),
    PolicySection(
        icon  = R.drawable.ic_phone,
        title = "الاتصال بنا",
        body  = "لأي استفسار أو طلب متعلق بخصوصيتك، يمكنك التواصل مع فريق الدعم عبر البريد الإلكتروني للمؤسسة أو من خلال قسم المساعدة داخل التطبيق. سيتم الرد على طلبك خلال 48 ساعة عمل.",
    ),
)

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun PrivacyPolicyScreen() {
    val backStack = StudentBackStackProvider.current

    LazyColumn(
        modifier       = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bgHover),
        contentPadding = PaddingValues(bottom = 40.dp),
    ) {

        // ── Hero header ───────────────────────────────────────────────────────
        item {
            PrivacyHero(onBack = { backStack.removeLastOrNull() })
        }

        // ── Last updated chip ─────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp, bottom = 4.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Row(
                    modifier              = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppTheme.color.primary.copy(alpha = .08f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Text(
                        text  = "آخر تحديث: يناير 2025",
                        color = AppTheme.color.primary,
                        style = AppTheme.textStyle.label.small.copy(fontWeight = FontWeight.Medium),
                    )
                    Icon(
                        painter  = painterResource(R.drawable.ic_clock),
                        tint     = AppTheme.color.primary,
                        modifier = Modifier.size(13.dp),
                    )
                }
            }
        }

        // ── Intro card ────────────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp, bottom = 4.dp)
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(16.dp), ambientColor = Color.Black.copy(.05f))
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppTheme.color.bg)
                    .padding(18.dp),
            ) {
                Text(
                    text      = "نحن في منصة رواء نُولي خصوصية بياناتك أهمية قصوى. تصف هذه السياسة كيفية جمع معلوماتك واستخدامها وحمايتها عند استخدامك لتطبيقنا التعليمي.",
                    color     = AppTheme.color.textSecondary,
                    style     = AppTheme.textStyle.body.small.copy(
                        fontWeight = FontWeight.Normal,
                        lineHeight = 22.sp,
                    ),
                    textAlign = TextAlign.End,
                    modifier  = Modifier.fillMaxWidth(),
                )
            }
        }

        // ── Policy sections ───────────────────────────────────────────────────
        item { Spacer(Modifier.height(8.dp)) }

        itemsIndexed(policySections) { index, section ->
            PolicySectionCard(
                section  = section,
                number   = index + 1,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 10.dp),
            )
        }

        // ── Footer agreement card ─────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 4.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppTheme.color.primary)
                    .padding(20.dp),
            ) {
                Column(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        painter  = painterResource(R.drawable.shield),
                        tint     = AppTheme.color.bg,
                        modifier = Modifier.size(32.dp),
                    )
                    Text(
                        text      = "باستخدامك للتطبيق فإنك توافق على سياسة الخصوصية هذه.",
                        color     = AppTheme.color.bg,
                        style     = AppTheme.textStyle.body.small.copy(
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp,
                        ),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text      = "نحتفظ بحق تحديث هذه السياسة في أي وقت. سيتم إخطارك بأي تغييرات جوهرية عبر الإشعارات داخل التطبيق.",
                        color     = AppTheme.color.bg.copy(alpha = .75f),
                        style     = AppTheme.textStyle.label.medium.copy(lineHeight = 18.sp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Hero header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PrivacyHero(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.color.primary)
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Back button (RTL — leading = end)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppTheme.color.bg.copy(alpha = .18f))
                    .clickAnimation(onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter  = painterResource(R.drawable.ic_arrow_forward),
                    tint     = AppTheme.color.bg,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        // Shield icon bubble
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(AppTheme.color.bg.copy(alpha = .15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter  = painterResource(R.drawable.shield),
                    tint     = AppTheme.color.bg,
                    modifier = Modifier.size(32.dp),
                )
            }
        }

        // Title + subtitle
        Column(
            modifier            = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text      = "سياسة الخصوصية",
                color     = AppTheme.color.bg,
                style     = AppTheme.textStyle.headline.small,
                textAlign = TextAlign.End,
            )
            Text(
                text      = "كيف نجمع بياناتك ونحميها",
                color     = AppTheme.color.bg.copy(alpha = .75f),
                style     = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                textAlign = TextAlign.End,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Policy section card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PolicySectionCard(
    section: PolicySection,
    number: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(16.dp), ambientColor = Color.Black.copy(.05f))
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── Card header ───────────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Section number badge
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(AppTheme.color.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text  = "$number",
                        color = AppTheme.color.bg,
                        style = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold),
                    )
                }

                // Spacer pushes icon+title to the right (RTL)
                Spacer(Modifier.weight(1f))

                // Title
                Text(
                    text  = section.title,
                    color = AppTheme.color.text,
                    style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
                )

                // Icon bubble
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppTheme.color.primary.copy(alpha = .10f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter  = painterResource(section.icon),
                        tint     = AppTheme.color.primary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Divider(color = AppTheme.color.border.copy(.4f), thickness = 0.8.dp)

            // ── Body text ─────────────────────────────────────────────────────
            Text(
                text      = section.body,
                color     = AppTheme.color.textSecondary,
                style     = AppTheme.textStyle.body.small.copy(
                    fontWeight = FontWeight.Normal,
                    lineHeight = 22.sp,
                ),
                textAlign = TextAlign.End,
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            )
        }
    }
}
