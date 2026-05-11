package noor.serry.rawaa.ui.screens.profile_teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.data.dto.CourseDto
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileTeacherScreen(
    viewModel: ProfileTeacherViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AppTheme.color.primary)
        }
        return
    }

    val user = state.user
    val profile = user?.profile

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bg),
        contentPadding = PaddingValues(bottom = 32.dp),
    ) {
        // Header with avatar & name
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppTheme.color.primaryDark)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            ) {
                Column {
                    Text(
                        text = "الملف الشخصي",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "معلوماتك الأكاديمية والمهنية",
                        color = Color.White.copy(0.75f),
                        fontSize = 13.sp,
                    )
                }
            }
        }

        // Profile card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-16).dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Avatar
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(AppTheme.color.primaryDark),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_person),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(44.dp),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(AppTheme.color.secondary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_person),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "د. ${user?.name ?: ""}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AppTheme.color.text,
                    )
                    Text(
                        text = profile?.departmentName ?: profile?.roleTitle ?: "",
                        fontSize = 13.sp,
                        color = AppTheme.color.textSecondary,
                    )
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AppTheme.color.secondary.copy(0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = profile?.roleTitle ?: "دكتوراه",
                            fontSize = 12.sp,
                            color = AppTheme.color.secondary,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = viewModel::onEditProfileClicked,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppTheme.color.primaryDark),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_person),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("تعديل الملف الشخصي", color = Color.White, fontSize = 13.sp)
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                    ) {
                        ProfileQuickStat(icon = R.drawable.ic_book, value = "10", label = "سنوات الخبرة")
                        ProfileQuickStat(icon = R.drawable.ic_person, value = "${state.totalStudents}", label = "إجمالي الطلاب")
                        ProfileQuickStat(icon = R.drawable.ic_book, value = "${state.activeCourses.size}", label = "المقررات الحالية")
                    }
                }
            }
        }

        // Academic info section
        item {
            SectionTitle(title = "المعلومات الأكاديمية", iconRes = R.drawable.ic_book)
        }

        item {
            InfoCard {
                InfoRow(label = "الرقم الوظيفي", value = "FAC-2020-001")
                InfoRow(label = "الكلية", value = profile?.departmentName ?: "كلية علوم الحاسب")
                InfoRow(label = "التخصص", value = profile?.roleTitle ?: "هندسة البرمجيات")
                InfoRow(label = "الدرجة العلمية", value = "دكتوراه")
                InfoRow(label = "سنوات الخبرة", value = "10 سنوات")
                InfoRow(label = "تاريخ الالتحاق", value = profile?.hireDate ?: "يناير 2020", isLast = true)
            }
        }

        // Contact info section
        item { SectionTitle(title = "معلومات الاتصال", iconRes = R.drawable.badge) }

        item {
            InfoCard {
                InfoRowWithEdit(
                    iconRes = R.drawable.badge,
                    label = "البريد الإلكتروني",
                    value = user?.email ?: "",
                )
                InfoRowWithEdit(
                    iconRes = R.drawable.ic_person,
                    label = "رقم الهاتف",
                    value = profile?.phone ?: "+20 123 456 7890",
                )
                InfoRowWithEdit(
                    iconRes = R.drawable.ic_home,
                    label = "المكتب",
                    value = "مبنى الهندسة - الدور الثالث - مكتب 305",
                )
                InfoRowWithEdit(
                    iconRes = R.drawable.ic_book,
                    label = "الساعات المكتبية",
                    value = "الأحد والثلاثاء: 10:00 - 12:00",
                    isLast = true,
                )
            }
        }

        // Current courses section
        if (state.activeCourses.isNotEmpty()) {
            item { SectionTitle(title = "المقررات الحالية", iconRes = R.drawable.ic_book) }

            items(state.activeCourses) { course ->
                ProfileCourseRow(course = course)
            }
        }
    }
}

@Composable
private fun ProfileQuickStat(icon: Int, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AppTheme.color.bg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = AppTheme.color.primary,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.text)
        Text(label, fontSize = 10.sp, color = AppTheme.color.textSecondary)
    }
}

@Composable
private fun SectionTitle(title: String, iconRes: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = AppTheme.color.primary,
            modifier = Modifier.size(18.dp),
        )
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.text)
    }
}

@Composable
private fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(4.dp), content = content)
    }
}

@Composable
private fun InfoRow(label: String, value: String, isLast: Boolean = false) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, fontSize = 13.sp, color = AppTheme.color.textSecondary)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AppTheme.color.text)
        }
        if (!isLast) HorizontalDivider(color = AppTheme.color.bg, thickness = 1.dp)
    }
}

@Composable
private fun InfoRowWithEdit(iconRes: Int, label: String, value: String, isLast: Boolean = false) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = AppTheme.color.textSecondary,
                modifier = Modifier.size(18.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 11.sp, color = AppTheme.color.textSecondary)
                Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AppTheme.color.text)
            }
            Icon(
                painter = painterResource(R.drawable.ic_person),
                contentDescription = "تعديل",
                tint = AppTheme.color.textSecondary,
                modifier = Modifier.size(16.dp).clickable { },
            )
        }
        if (!isLast) HorizontalDivider(color = AppTheme.color.bg, thickness = 1.dp)
    }
}

@Composable
private fun ProfileCourseRow(course: CourseDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_person),
                    contentDescription = null,
                    tint = AppTheme.color.textSecondary,
                    modifier = Modifier.size(20.dp),
                )
                Column {
                    Text(course.name, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = AppTheme.color.text)
                    Text(
                        text = "طالب ${course.enrolledCount ?: 0} • ${course.code}",
                        fontSize = 11.sp,
                        color = AppTheme.color.textSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementBadge(modifier: Modifier, title: String, year: String, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(R.drawable.badge),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp),
            )
            Spacer(Modifier.height(6.dp))
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
            Text(year, fontSize = 11.sp, color = color.copy(0.7f))
        }
    }
}
