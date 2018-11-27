<?php

use Illuminate\Database\Seeder;

class BusinessCardsTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        factory(\App\BusinessCard::class, 10)->create();
    }
}
