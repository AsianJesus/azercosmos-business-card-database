<?php

/*
|--------------------------------------------------------------------------
| Model Factories
|--------------------------------------------------------------------------
|
| Here you may define all of your model factories. Model factories give
| you a convenient way to create models for testing and seeding your
| database. Just tell the factory how a default model should look.
|
*/

$factory->define(\App\BusinessCard::class, function (Faker\Generator $faker) {
    return [
        'name' => $faker->firstName,
//        'surname' => $faker->lastName,
        'company_name' => $faker->company,
        'position' => $faker->jobTitle,
        'address' => $faker->address,
        'mobile' => $faker->phoneNumber,
        'email' => $faker->email,
        'website' => $faker->domainName,
        'created_by' => rand(1,3),
        'private' => rand(1,2) == 1
    ];
});
