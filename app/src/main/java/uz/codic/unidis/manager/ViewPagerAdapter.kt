package uz.codic.unidis.manager

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class ViewPagerAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(p0: Int): Fragment {
        when(p0){
            0-> return GeneralFragment.newInstance()
            1-> return StatisticsFragment.newInstance()
        }
        return GeneralFragment.newInstance()
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if(position == 0) "Umumiy" else "Statistika"
    }
}