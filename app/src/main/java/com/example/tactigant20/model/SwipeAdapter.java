package com.example.tactigant20.model;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

/**
 * Classe gérant le balayage entre fragments
 * Cet objet sert à stocker les différents fragments d'une activité dans une liste
 * On parcourt cette liste en même temps qu'on se déplace dans l'application
 * Cela permet de concilier le <i>Swipe</i> avec d'autres méthodes de navigation au sein de l'application
 *
 * @author Roman T.
 * @since 1.0
 */
public class SwipeAdapter extends FragmentStateAdapter {

    private final ArrayList<Fragment> fragmentList = new ArrayList<>();

    /**
     * Constructeur unique du <i>SwipeAdapter</i>
     *
     * @param fragmentManager le <i>FragmentManager</i> de l'activité (appeler <i>getSupportFragmentManager()</i>)
     * @param lifecycle       le <i>LifeCycle</i> de l'activité (appeler <i>getLifecycle()</i>)
     */
    public SwipeAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    /**
     * Renvoie un fragment situé dans la liste à partir de son indice
     *
     * @param position l'indice du fragment auquel on souhaite accéder
     * @return le fragment auquel on souhaite accéder
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    /**
     * Ajoute un fragment dans la liste
     *
     * @param fragment le fragment à ajouter
     */
    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
    }

    /**
     * Renvoie la taille de la liste
     *
     * @return la taille de la liste
     */
    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}