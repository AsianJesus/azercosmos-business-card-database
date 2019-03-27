<?php

namespace App;


use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class BusinessCard extends Model
{
    use SoftDeletes;
    protected $fillable = [
        'name', 'company_name', 'position', 'address', 'private',
        'mobile', 'email', 'website', 'image_path', 'created_by', 'deleted_by'
    ];

    protected $table = 'business_cards';

    public function created_by()
    {
        return $this->belongsTo(User::class, 'created_by');
    }

    public function permissions()
    {
        return $this->hasMany(PermissionUser::class, 'business_card_id');
    }

}