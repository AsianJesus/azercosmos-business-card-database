<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class User extends Model
{
    protected $fillable = [];
    protected $table;

    public function __construct()
    {
        $this->table = env('DB_ERP', 'admin_erp') . '.PEOPLE';
    }
}
