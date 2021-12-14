package com.ceydakamali.oopproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User myUser= new User("Ceyda","Student");
        System.out.println(myUser.name);
        //Abstract
        System.out.println(myUser.information());

        //Encapsulation
        Musician james= new Musician("James","Guitar",50);
        james.setAge(60);
        System.out.println(james.getAge());

        //Inheritance
        SuperMusician lars= new SuperMusician("Lars","Drums",55);
        System.out.println(lars.sing());

        //Polymorphism

        //Static Polymorphism: aynı sınıfta aynı isimli metotlar kullanarak farklı sonuçlar elde edebiliyoruz.
        Mathematics mathematics= new Mathematics();
        System.out.println(mathematics.sum());
        System.out.println(mathematics.sum(5,3));
        System.out.println(mathematics.sum(5,3,4));

        //Dynamic Polymorphism
        Animal animal=new Animal();
        animal.sing();
        Dog dog=new Dog();
        dog.test();
        dog.sing();

        //Interface
        Piano piano=new Piano();
        piano.brand="Yamaha";
        piano.digital=true;
        piano.info();


    }
}