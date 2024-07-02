package eramo.resultgate.presentation.adapters.viewpager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import dagger.hilt.android.qualifiers.ApplicationContext
import eramo.resultgate.R
import eramo.resultgate.domain.model.auth.OnBoardingModel
import javax.inject.Inject

class OnBoardingAdapter @Inject constructor(@ApplicationContext private var context: Context) :
    PagerAdapter() {

    private var screensList = emptyList<OnBoardingModel>()

    fun setScreens(list: List<OnBoardingModel>) {
        this.screensList = list
        notifyDataSetChanged()
    }

    override fun getCount(): Int = screensList.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean =
        view == `object` as ConstraintLayout

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View =
            layoutInflater.inflate(R.layout.layout_onboarding_sliding, container, false)
        val imageView = view.findViewById<ImageView>(R.id.slider_img)
        val tvHeading = view.findViewById<TextView>(R.id.heading)
        val tvBody = view.findViewById<TextView>(R.id.body)
        Glide.with(context)
//            .load((EramoApi.IMAGE_URL_SPLASH + screensList[position].image))
            .load(screensList[position].image)
            .into(imageView)
        tvHeading.text = screensList[position].splashTitle
        tvBody.text  = screensList[position].splashDetails
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}