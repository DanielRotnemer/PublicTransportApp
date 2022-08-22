package com.crosstown.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.crosstown.R;

public class LoginSignupActivity extends AppCompatActivity
{
    private TextPaint textPaint;
    private Context context = this;
    private AlertDialog.Builder alert;
    private int displayHeight, displayWidth;

    private ConstraintLayout mainLoginSignupLayout, loginSignupDashboardContainer, loginDashboard,
            signupDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.dark_color));
        }

        mainLoginSignupLayout = findViewById(R.id.mainLoginSignupLayout);
        mainLoginSignupLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                displayHeight = mainLoginSignupLayout.getHeight();
                displayWidth = mainLoginSignupLayout.getWidth();
            }
        });

        setTermsSpan((TextView)findViewById(R.id.terms));
        setTermsSpan((TextView)findViewById(R.id.loginTerms));
        setTermsSpan((TextView)findViewById(R.id.signupTerms));

        setLoginLinkClickEvent();
        setSignupLinkClickEvent();

        loginSignupDashboardContainer = findViewById(R.id.loginSignupDashboardContainer);
        loginDashboard = findViewById(R.id.loginDashboard);
        signupDashboard = findViewById(R.id.signupDashboard);
    }

    private void setTermsSpan(TextView termsTextView)
    {
        String termsText = getResources().getString(R.string.terms);

        if (termsText.equals("By signing up, you agree to Music's Terms of service and Privacy policy."))
        {
            SpannableStringBuilder spanTxt = new SpannableStringBuilder("By signing up, you agree to Music's ");
            spanTxt.append("Terms of service ");
            spanTxt.setSpan(new ClickableSpan()
            {
                @Override
                public void onClick(View widget)
                {
                    alert = new AlertDialog.Builder(context);
                    alert.setMessage("Terms clicked");
                    alert.show();
                }

                @Override
                public void updateDrawState(TextPaint ds)
                {
                    textPaint = ds;
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            }, spanTxt.length() - "Terms of service ".length(), spanTxt.length(), 0);

            spanTxt.append("and");

            spanTxt.append(" Privacy Policy");
            spanTxt.setSpan(new ClickableSpan()
            {
                @Override
                public void onClick(View widget)
                {
                    alert = new AlertDialog.Builder(context);
                    alert.setMessage("Privacy clicked");
                    alert.show();
                }

                @Override
                public void updateDrawState(TextPaint ds)
                {
                    textPaint = ds;
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            }, spanTxt.length() - " Privacy Policy".length(), spanTxt.length(), 0);

            spanTxt.append(".");

            termsTextView.setMovementMethod(LinkMovementMethod.getInstance());
            termsTextView.setText(spanTxt, TextView.BufferType.SPANNABLE);
        }
        else if (termsText.equals("בעצם הרשמתך, אתה מסכים לתנאי השימוש ולמדיניות הפרטיות של Music."))
        {
            SpannableStringBuilder spanTxt = new SpannableStringBuilder("בעצם הרשמתך, אתה מסכים ל");
            spanTxt.append("תנאי השימוש");
            spanTxt.setSpan(new ClickableSpan()
            {
                @Override
                public void onClick(View widget)
                {
                    alert = new AlertDialog.Builder(context);
                    alert.setMessage("Terms clicked");
                    alert.show();
                }

                @Override
                public void updateDrawState(TextPaint ds)
                {
                    textPaint = ds;
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            }, spanTxt.length() - "תנאי השימוש".length(), spanTxt.length(), 0);

            spanTxt.append(" ול");

            spanTxt.append("מדיניות הפרטיות");
            spanTxt.setSpan(new ClickableSpan()
            {
                @Override
                public void onClick(View widget)
                {
                    alert = new AlertDialog.Builder(context);
                    alert.setMessage("Privacy clicked");
                    alert.show();
                }

                @Override
                public void updateDrawState(TextPaint ds)
                {
                    textPaint = ds;
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            }, spanTxt.length() - "מדיניות הפרטיות".length(), spanTxt.length(), 0);

            spanTxt.append(" של Music.");

            termsTextView.setMovementMethod(LinkMovementMethod.getInstance());
            termsTextView.setText(spanTxt, TextView.BufferType.SPANNABLE);
        }
    }

    public void showLoginPanel()
    {
        if (loginSignupDashboardContainer.getVisibility() == View.VISIBLE)
            return;
        loginDashboard.setVisibility(View.VISIBLE);
        signupDashboard.setVisibility(View.GONE);
        TranslateAnimation anim = new TranslateAnimation(0, 0, displayHeight, 0);
        anim.setDuration(600);
        anim.setFillEnabled(false);
        anim.setFillAfter(false);
        loginSignupDashboardContainer.startAnimation(anim);
        loginSignupDashboardContainer.setVisibility(View.VISIBLE);
    }

    public void showSignupPanel()
    {
        if (loginSignupDashboardContainer.getVisibility() == View.VISIBLE)
            return;
        signupDashboard.setVisibility(View.VISIBLE);
        loginDashboard.setVisibility(View.GONE);
        TranslateAnimation anim = new TranslateAnimation(0, 0, displayHeight, 0);
        anim.setDuration(600);
        anim.setFillEnabled(false);
        anim.setFillAfter(false);
        loginSignupDashboardContainer.startAnimation(anim);
        loginSignupDashboardContainer.setVisibility(View.VISIBLE);
    }

    public void closeLoginSignupPanel()
    {
        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, displayHeight);
        anim.setDuration(600);
        anim.setFillEnabled(false);
        anim.setFillAfter(false);
        loginSignupDashboardContainer.startAnimation(anim);
        loginSignupDashboardContainer.setVisibility(View.GONE);
    }

    private void setLoginLinkClickEvent()
    {
        String loginSwitcherText = " Have an account? Log in ";
        SpannableString ss = new SpannableString(loginSwitcherText);
        ClickableSpan loginClick = new ClickableSpan()
        {
            @Override
            public void onClick(@NonNull View widget)
            {
                TextView tv = (TextView)widget;
                if (tv.getText().toString() == " Have an account? Log in ")
                {
                    Selection.setSelection((Spannable)tv.getText(), 0);
                    textPaint.bgColor = Color.TRANSPARENT;
                    widget.invalidate();

                    TranslateAnimation anim1 = new TranslateAnimation(-displayWidth, 0, 0, 0);
                    TranslateAnimation anim2 = new TranslateAnimation(0, displayWidth, 0, 0);
                    anim1.setDuration(400);
                    anim2.setDuration(400);
                    anim1.setFillEnabled(false);
                    anim1.setFillAfter(false);
                    anim2.setFillEnabled(false);
                    anim2.setFillAfter(false);

                    loginDashboard.startAnimation(anim1);
                    loginDashboard.setVisibility(View.VISIBLE);
                    signupDashboard.startAnimation(anim2);
                    signupDashboard.setVisibility(View.GONE);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds)
            {
                textPaint = ds;
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        TextView signupSwitcher = findViewById(R.id.signupSwitcher);
        ss.setSpan(loginClick, 18, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signupSwitcher.setText(ss);
        signupSwitcher.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setSignupLinkClickEvent()
    {
        String loginSwitcherText = " Don't have an account? Create account ";
        SpannableString ss = new SpannableString(loginSwitcherText);
        ClickableSpan loginClick = new ClickableSpan()
        {
            @Override
            public void onClick(@NonNull View widget)
            {
                TextView tv = (TextView)widget;
                if (tv.getText().toString() == " Don't have an account? Create account ")
                {
                    Selection.setSelection((Spannable)tv.getText(), 0);
                    textPaint.bgColor = Color.TRANSPARENT;
                    widget.invalidate();

                    TranslateAnimation anim1 = new TranslateAnimation(0, -displayWidth, 0, 0);
                    TranslateAnimation anim2 = new TranslateAnimation(displayWidth, 0, 0, 0);
                    anim1.setDuration(400);
                    anim2.setDuration(400);
                    anim1.setFillEnabled(false);
                    anim1.setFillAfter(false);
                    anim2.setFillEnabled(false);
                    anim2.setFillAfter(false);
                    loginDashboard.startAnimation(anim1);
                    loginDashboard.setVisibility(View.GONE);
                    signupDashboard.startAnimation(anim2);
                    signupDashboard.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds)
            {
                textPaint = ds;
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        TextView loginSwitcher = findViewById(R.id.loginSwitcher);
        ss.setSpan(loginClick, 24, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginSwitcher.setText(ss);
        loginSwitcher.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void signup_OnClick(android.view.View view) {
        showSignupPanel();
    }

    public void login_OnClick(android.view.View view) {
        showLoginPanel();
    }

    public void loginSignupDashboardContainer_OnClick(android.view.View view) {
        closeLoginSignupPanel();
    }

    public void loginSignupDashboardWrapper_OnClick(android.view.View view) { }

    public void loginEmailPasswordButton_OnClick(android.view.View view)
    {
        Intent intent = new Intent(getBaseContext(), EmailPhoneLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}