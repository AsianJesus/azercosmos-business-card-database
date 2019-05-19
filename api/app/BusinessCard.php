<?php

namespace App;


use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Support\Str;

class BusinessCard extends Model
{
    use SoftDeletes;
    protected $fillable = [
        'name', 'company_name', 'position', 'address', 'private',
        'mobile', 'email', 'website', 'image_path', 'created_by', 'deleted_by'
    ];

    protected $with = [
        'notes'
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

    public function notes() {
        return $this->hasMany(Note::class);
    }

    protected static function boot()
    {
        parent::boot();

        self::creating(function ($post) {
            $post->{$post->getKeyName()} = $post->{$post->getKeyName()} ?? Str::uuid();
        });
    }

    public function getKeyName()
    {
        return 'id';
    }

    public function getKeyType()
    {
        return 'string';
    }

    public function getIncrementing()
    {
        return false;
    }

}