<?php
/**
 * Created by PhpStorm.
 * User: fruit
 * Date: 11/26/2018
 * Time: 11:50 PM
 */

namespace App;


use Illuminate\Database\Eloquent\Model;

class BusinessCard extends Model
{
    protected $fillable = [
        'name', 'surname', 'company_name', 'position', 'address',
        'mobile', 'email', 'website', 'created_by', 'deleted_by'
    ];

    protected $table = 'business_cards';

    public function created_by() {
        return $this->belongsTo(User::class, 'created_by');
    }

    public function permissions() {
        return $this->hasMany(PermissionUser::class, 'business_card_id');
    }

}