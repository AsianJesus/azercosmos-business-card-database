<?php

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class PermissionsTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        $permissions = ['Read', 'Add/Edit', 'Delete',];
        foreach ($permissions as $permission)
        {
            DB::table('permissions')->insert(['name' => $permission]);
        }
    }
}
